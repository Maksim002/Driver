package ru.telecor.gm.mobile.droid.model.interactors

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.processing.TaskFinishType
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.request.FinishRouteData
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.model.data.server.GmWebSocket
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.ServerError
import ru.telecor.gm.mobile.droid.model.entities.ExportedDataInfo
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.repository.PhotoRepository
import ru.telecor.gm.mobile.droid.model.repository.RouteRepository
import ru.telecor.gm.mobile.droid.model.system.LocationProvider
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatus
import ru.telecor.gm.mobile.droid.entities.request.ArrivalRequest
import ru.telecor.gm.mobile.droid.model.repository.OperationType
import ru.telecor.gm.mobile.droid.model.workers.PHOTO_SYNC_UNIQUE_WORK_NAME
import ru.telecor.gm.mobile.droid.model.workers.PhotoSynchronizationWorker
import ru.telecor.gm.mobile.droid.model.workers.UploadWorker
import ru.telecor.gm.mobile.droid.presentation.exitDialog.ExitDialogView
import ru.telecor.gm.mobile.droid.utils.LogUtils
import ru.terrakok.cicerone.Router
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 * Interactor for route features.
 * Use cases:
 * - Starting route
 * - Get current route
 * - Get tasks for current route
 * - Get task by ID
 * - Get available loaders
 * - Set first and second loader
 * - Get available polygon's
 * - Go to polygon
 * - Add task processing result
 * - Check is all data was send to server (photos, task processing results)
 * - Finishing route
 *
 * Created by Artem Skopincev (aka sharpyx) 03.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class RouteInteractor @Inject constructor(
    private val routeRepository: RouteRepository,
    private val photoInteractor: PhotoInteractor,
    private val commonDataRepository: CommonDataRepository,
    private val gmWebSocket: GmWebSocket,
    private val locationProvider: LocationProvider,
    private val context: Context,
    private val router: Router,
    private val reportBase: ReportBase,
    private val photoRepository: PhotoRepository
) {

    private var idValue = MutableLiveData(0L)
    val containerStatus = ArrayList<ContainerStatus>()
    var isEdited = false
    var taskId:Int = 0
    var photoLoader: ((bol: Boolean) -> Unit?)? = null

    var namefir = MutableLiveData<LoaderInfo>()
    var nameSec = MutableLiveData<LoaderInfo>()
    var processingArrivalTime: Long? = null

    init {


        //TODO wsChange fallBack version
        gmWebSocket.addOnMessageReceivedListener { msg ->
            GlobalScope.launch {
                loadTasksOnMessage(msg)
                fetchTasksWithServer(false)
            }
        }

        //Получение от сокетов веса
        gmWebSocket.addOnMessageTalonListener { webTalon ->
            onTalonListener?.invoke(webTalon)
        }

        gmWebSocket.setOnOpenListener { onSocketStateChangeListener?.invoke(true) }
        gmWebSocket.setOnCloseListener { onSocketStateChangeListener?.invoke(false) }
        gmWebSocket.checkListen()

        // wsChange  - Постоянная синхронизация
        /*
        startPhotoSynchronizationWorker()*/
        startRouteSynchronization()
    }

    private fun startPhotoSynchronizationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<PhotoSynchronizationWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                PHOTO_SYNC_UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
    }

    /**
     * На некоторых инстансах сокет отваливается, поэтому приходится вот так вот обновлять все руками
     */
    private fun startRouteSynchronization() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                fetchTasksWithServer(false)
                delay(60000)
            }
        }
    }

    suspend fun getTaskWithRelations() = routeRepository.getTasksWithRelations()

    val startedRoute: RouteInfo?
        get() = routeRepository.startedRoute

    fun getCarryContainers() = routeRepository.currentCarryContainers

    val TAG = javaClass.simpleName
    private var isRouteStarted: Boolean = false
    private var onTasksUpdatedListeners: MutableList<(tasks: List<TaskExtended>) -> Unit> =
        mutableListOf()
    private var onSendingDataListener: ((isSending: Boolean) -> Unit)? = null
    private var onSocketStateChangeListener: ((isOpen: Boolean) -> Unit)? = null
    private var onTalonListener: ((webSocketTalon: WebSocketTalon) -> Unit)? = null

    fun setCarryContainersListener(listener: (List<CarryContainer>) -> Unit) {
        routeRepository.carryContainersListener = listener
    }

    private var mobileSystemOptions: MobileSystemOptions? = null

    fun addOnTasksUpdatedListener(listener: (tasks: List<TaskExtended>) -> Unit) {
        onTasksUpdatedListeners.add(listener)
    }

    fun removeTasksUpdatedListener(listener: (tasks: List<TaskExtended>) -> Unit) {
        onTasksUpdatedListeners.remove(listener)
    }

    fun setOnSendingListener(listener: (isSending: Boolean) -> Unit) {
        onSendingDataListener = listener
    }

    fun setOnSocketStateChangeListener(listener: (isOpen: Boolean) -> Unit) {
        onSocketStateChangeListener = listener
    }

    fun setOnTalonListener(listener: (webSocketTalon: WebSocketTalon) -> Unit) {
        onTalonListener = listener
    }

    private fun sendTaskUpdated(tasks: List<TaskExtended>) {
        if (onTasksUpdatedListeners.isNullOrEmpty()) return
        try {
            onTasksUpdatedListeners.map {
                it.invoke(tasks)
            }
        } catch (e: Exception) {
            LogUtils.error(TAG, e.message)
        }

    }

    /**
     * @return list of [RouteInfo] for current tour
     */
    fun getAvailableRoutes() = routeRepository.getRoutes()


    /**
     * Starting route. Before this load all tasks and stands.
     * After that available updated current route info.
     * @param routeInfo
     */
    suspend fun startRoute(routeInfo: RouteInfo): Result<RouteInfo> {

        val startedRouteData = startedRoute
        CoroutineScope(Dispatchers.IO).launch {
            if (startedRouteData == null) {
                photoInteractor.uploadAllUndeliveredPhotos()
                photoRepository.cleanOldPhotos()
            } else {
                if (ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.FILE)) {
                    photoInteractor.uploadAllUndeliveredPhotos()
                }
            }
        }

        if (startedRoute?.id != routeInfo.id) {
            clearCache()
        }
        if ((routeRepository.getTaskCountDb() == 0)) {
            val reResult = loadTasksForCurrentRoute(routeInfo.id, true)
            if (reResult is Result.Error) return reResult
        } else loadTasksForCurrentRoute(routeInfo.id)



        idValue.value = routeInfo.id

        val result: Result<RouteInfo> =
            if (ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.SECONDARY)) {
                if (routeInfo.status.name == RouteStatusType.INPROGRESS) {
                    routeRepository.continueRoute(routeInfo.id)
                } else {
                    routeRepository.startRoute(routeInfo.id)
                }
            } else
                Result.Error(Exception("Низкая скорость интернета"))

        if (ConnectivityUtils.syncAvailability(context)) {
            fetchTasksWithServer()
        }

        gmWebSocket.startListen()
        isRouteStarted = true

        //Отправка грусчиков
        if (namefir.value != null){
            setLoader(1, namefir.value)
        }
        if (nameSec.value != null){
            setLoader(2, nameSec.value)
        }


        if (startedRouteData == null || ConnectivityUtils.syncAvailability(context,ConnectivityUtils.DataType.SECONDARY)) {

            routeRepository.fetchContainersHistory()
            if (result is Result.Success) {
                routeRepository.sendHeartbeat(result.data.id)
                val res1 = getContainerTroubleReasons(true)
                val res2 = getTroubleReasons(true)

                return when (res1) {
                    is Result.Success -> {
                        when (res2) {
                            is Result.Success -> result
                            is Result.Error -> res2
                        }
                    }
                    is Result.Error -> res1
                }
            }
        }
        return result
    }

    fun getTaskCountDb() = routeRepository.getTaskCountDb()

    suspend fun removeTask(curTask: TaskExtended) {
        if (curTask.visitPoint != null) {
            routeRepository.removeTask(curTask)
        }
    }

    suspend fun getPhotoRequest(id: String) = routeRepository.getPhotoRequest(id)

    suspend fun getStartedRouteFromServer(): Result<RouteInfo> {
        return routeRepository.getStartedRouteFromServer()
    }

    fun addCarryContainer(carryContainer: CarryContainer) =
        routeRepository.addCarryContainer(carryContainer)

    fun removeCarryContainer(carryContainer: CarryContainer) =
        routeRepository.removeCarryContainer(carryContainer)

    /**
     * Get tasks for current route
     * @return list of [TaskExtended]
     */
    suspend fun getTasksForCurrentRoute(): List<TaskExtended> {
        return routeRepository.getTasksForRoute(routeRepository.startedRoute!!.id) ?: listOf()
    }

    fun getTasksForCurrentRouteFromCache(): List<TaskExtended> {
        return routeRepository.getCurrentTasksAsCache()
    }

    /**
     * @return current [TaskExtended] of the route
     */
    fun getCurrentTask() = routeRepository.getCurrentTask()

    suspend fun getDraftByTaskID(taskId: Long) = routeRepository.getDraftByTaskID(taskId)

    suspend fun getFlightTicketModel() =
        routeRepository.getFlightTicketModel(idValue.value)


    fun getCurrentRoute(): RouteInfo {
        return routeRepository.startedRoute ?: throw Exception("route is null")
    }

    fun getCurrentRouteStart(): RouteInfo? {
        return routeRepository.startedRoute
    }

    fun getContext(): Context = context

    private suspend fun loadTasksOnMessage(
        msg: WebSocketMsg
    ): MutableList<TaskExtended>? {
        val r = routeRepository.updateRouteByMessage(msg)
        if (r != null) {
            val cRoute = startedRoute
            sendTaskUpdated(r)
            if (routeRepository.getRecalculateType() == OperationType.REFORM && cRoute != null)
                loadTasksForCurrentRoute(cRoute.id, true)
        }
        return r
    }

    fun getCurrentTaskSimply() = routeRepository.getCurrentTaskSimply()

    /**
     * @return [TaskExtended] by taskId
     */
    fun getTaskById(id: Int) = routeRepository.getTaskById(id)

    fun getCarryContainersHistory(): List<CarryContainerHistoryItem>? =
        routeRepository.carryContainersHistory

    /**
     * Select task by taskId as next, send request to server.
     * After that, list of tasks will be updated.
     * @param taskId ID of task that need to choose as next
     */
    suspend fun selectTaskAsNext(taskId: Long) {
        GlobalScope.launch {
            if (routeRepository.reorderTasks(taskId) is Result.Success
                && ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.SECONDARY)
            ) fetchTasksWithServer()
        }
    }


    /**
     * Finish the route and tour.
     * Take valid param and lat, lon coordinates.
     * @return [ResponseBody] if ok - 200.
     */
    suspend fun finishRoute(finishRouteData: FinishRouteData): Result<ResponseBody> {
        val taskProcessingResults = routeRepository.getTaskProcessingResults()
        // if we have undelivered results, send them to server
        if (taskProcessingResults != null) {
            for (r in taskProcessingResults) {
                var succeeded = false
                var i = 0
                while (!succeeded) {

                    val result = sendProcessingResult(r)

                    val deliveredTask =
                        r.processingStatus == TaskProcessingResult.ProcessingStatus.DELIVERED && i == 2

                    i++
                    if (result is Result.Error && !deliveredTask) continue

                    succeeded = true
                }
            }

            //routeRepository.clearTaskProcessingList()
        }

        return routeRepository.finishRoute(finishRouteData)
    }

    suspend fun polygonCancel(routeId: Int): Result<ResponseBody> {
        return routeRepository.polygonCancel(routeId)
    }

    suspend fun getPossiblePorters(includePhoto: Boolean): Result<List<LoaderInfo>> =
        commonDataRepository.getAvailablePorters(includePhoto)

    suspend fun getStartedRouteInfo(
        fromServer: Boolean = false,
        syncAval: Boolean? = null,
        id: Long = 0
    ) =
        routeRepository.getStartedRoute(
            fromServer,
            syncAval ?: ConnectivityUtils.syncAvailability(context),
            id
        )

    suspend fun setLoader(num: Int, loaderInfo: LoaderInfo?): Result<ResponseBody> =
        routeRepository.setLoader(num, loaderInfo)

    suspend fun getTroubleReasons(primarilyFromServer: Boolean = false): Result<List<TaskFailureReason>> =
        commonDataRepository.getTaskFailureReasons(primarilyFromServer)

    suspend fun getContainerTroubleReasons(primarilyFromServer: Boolean = false) =
        commonDataRepository.getContainerFailureReasons(primarilyFromServer)

    suspend fun getNearStands(lat: Double, lon: Double) = routeRepository.getNearStands(lat, lon)

    suspend fun loadTasksForCurrentRoute(
        routeId: Long,
        refresh: Boolean = false,
        recycFirst: Boolean = false
    ): Result<List<TaskExtended>> {
        val r = routeRepository.loadTasksForRoute(routeId, refresh, recycFirst)

        // send event to listeners
        if (r is Result.Success) {
            sendTaskUpdated(r.data)
        }

        return r
    }

    /**
     * Add task processing result to list and try to send it to server.
     * If sending to server is not successful, then method save this result to list of results.
     * @return true is successful sending to server and false if not.
     */
    @Suppress("RedundantIf")
    suspend fun addTaskToProcessing(
        task: TaskExtended,
        status: ProcessingStatusType,
        standResults: List<StandResult>,
        taskFailureReason: TaskFailureReason?,
        forceLoad: Boolean = false
    ): Result<Boolean> {

        val routeId = routeRepository.startedRoute?.id ?: throw Exception("started route is null")

        val taskPhotosCount =
            photoRepository.allPhotosFlow.value.filter { (it.routeId == routeId) and (it.taskId == task.id.toLong()) }.size

        val result = TaskProcessingResult(
            task.id.toLong(),
            routeId,
            status,
            taskFailureReason,
            null,
            standResults,
            null,
            taskPhotosCount > 0,
            if (taskPhotosCount > 0) taskPhotosCount else null,
            task.visitPoint?.id,
            TaskFinishType.Driver
        )

        val list = listOf(result)


        photoInteractor.setTaskPhotosExportable(routeId, task.id.toLong())
        if (forceLoad) {
            onSendingDataListener?.invoke(true)
            return if (task.visitPoint != null) {
                val apiResult = routeRepository.processStandResults(routeId, list)
                return if (apiResult is Result.Success) {
                    setStatusAndNextTask(task, status)
                    routeRepository.commitDeliveredTask(result)
                    loadTasksForCurrentRoute(routeId, true)

                    onSendingDataListener?.invoke(false)

                    Result.Success(true)
                } else {
                    onSendingDataListener?.invoke(false)

                    apiResult as Result.Error
                }
            } else {
                routeRepository.rCulateCurrentTask()
                loadTasksForCurrentRoute(routeId, true)
                Result.Success(true)
            }

        } else {
            routeRepository.addTaskResultToProcessingList(result)
            setStatusAndNextTask(task, status)
            loadTasksForCurrentRoute(routeId, false)
            fetchTasksWithServer(false)
            return Result.Success(true)
        }
    }

    @Suppress("RedundantIf")
    suspend fun addDraftTaskToProcessing(
        task: TaskExtended,
        status: ProcessingStatusType,
        standResults: List<StandResult>,
        taskFailureReason: TaskFailureReason?,
    ) {
        try {
            val routeId = routeRepository.startedRoute?.id
            if (routeId != null) {
                val result = TaskDraftProcessingResult(
                    id = task.id.toLong(),
                    routeId = routeId,
                    statusType = status,
                    failureReason = taskFailureReason,
                    actualReason = null,
                    standResults = standResults,
                    detourPointProcessingResults = null,
                    hasPhotos = false,
                    photosCount = null,
                    visitPointId = task.visitPoint?.id,
                    taskFinishType = TaskFinishType.Driver
                )

                routeRepository.addTaskResultToDraftProcessingList(result)
            }
        } catch (e: Exception) {
            LogUtils.error(TAG, e.message)
        }
    }


    // TODO Я считаю что этот метод можно внедрить в работу большиснтва других
    /**
     * Асинхронно загружает все незагруженные таски на сервер, и, если смог загрузить все,
     * Обновляет список заданий
     */
    fun fetchTasksWithServer(loadTasks: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            onSendingDataListener?.invoke(true)
            val routeId = routeRepository.startedRoute?.id
            routeId?.let {
                val undeliveredTasks = routeRepository.getUndeliveredTaskResults()
                if (!undeliveredTasks.isNullOrEmpty()) {
                    for (ut in undeliveredTasks) {
                        val resultd = listOf(ut)
                        val apiResult = routeRepository.processStandResults(it, listOf(ut))
                        if (apiResult is Result.Success) {
                            routeRepository.removeTaskFromProcessingList(ut)
                        } else {
                            return@let
                        }
                    }
                }
                if (loadTasks) {
                    loadTasksForCurrentRoute(routeId, true)
                }
            }
            onSendingDataListener?.invoke(false)
        }
    }

    private fun setStatusAndNextTask(task: TaskExtended, status: ProcessingStatusType) {
        routeRepository.setStatusAndNextTask(task, status.name)
    }

    private suspend fun sendProcessingResult(taskProcessingResult: TaskProcessingResult): Result<ResponseBody> {
        val routeId =
            routeRepository.startedRoute?.id ?: return Result.Error(Exception("route id is null"))
        val result = routeRepository.processStandResults(routeId, listOf(taskProcessingResult))
        if (result is Result.Success) {
            if (taskProcessingResult.processingStatus == TaskProcessingResult.ProcessingStatus.PROCESSING) {
                routeRepository.removeTaskFromProcessingList(taskProcessingResult)
            }
        }
        return result
    }

    suspend fun sendAllInfoToServer(): Flow<ExportedDataInfo> = flow {

        val currentStartedRoute = routeRepository.startedRoute

        if (currentStartedRoute == null) {
            emit(getExportedDataInfo().apply {
                workStatus = ExitDialogView.WorkStatus.ERROR
                loadStatus = ExitDialogView.LoadStatus.ERROR
                errorMessage = "Ошибка, попробуйте перезапустить приложение!"
                destroyPhotoCount = 0
                isError = true
            })
            return@flow
        }
        val routeId = currentStartedRoute.id

        val isCon = ConnectivityUtils.syncAvailability(context)
        val availToFin = routeRepository.prepareFinishRoute() is Result.Success

        if (!isCon || !availToFin) {
            emit(getExportedDataInfo().apply {
                workStatus = ExitDialogView.WorkStatus.ERROR
                loadStatus = ExitDialogView.LoadStatus.ERROR
                errorMessage =
                    if (!isCon) "Недостаточная скорость интернета для завершения маршрута. Подключитесь более скоростной сети!" else "Сервер не доступен"
                destroyPhotoCount = 0
                isError = true
            })
            return@flow
        }

        //Очистка не актуальнцых фотографий, перед выгрузкой
        photoRepository.cleanUpTempDirectory()

        emit(getExportedDataInfo().apply {
            workStatus = ExitDialogView.WorkStatus.PREPARE
            loadStatus = ExitDialogView.LoadStatus.INDEFINITE
        })

        // Востоновление метаданных по фотографиям если приложение было удалено START
        photoRepository.recoveryMetadataSyn()

        // Востоновление крашенных фотографий
        photoRepository.recoveryPhotoFiles(photoRepository.allPhotosFlow.value.filter { it.exportStatus != ProcessingPhoto.ExportStatus.SENT })

        routeRepository.fetchContainersHistory()

        var finished = true

        // get undelivered tasks and send to server
        val undeliveredTasks = routeRepository.getUndeliveredTaskResults()

        val undeliveredPhotos = photoRepository.allPhotosFlow.value.filter {
            it.exportStatus != ProcessingPhoto.ExportStatus.SENT && !photoRepository.checkPhotoCrash(
                it.photoPath
            )
        }
        if (!undeliveredTasks.isNullOrEmpty() || !undeliveredPhotos.isNullOrEmpty()) {

            emit(getExportedDataInfo().apply { workStatus = ExitDialogView.WorkStatus.UPLOAD })

            for (ut in undeliveredTasks) {
                val apiResult = routeRepository.processStandResults(routeId, listOf(ut))
                if (apiResult is Result.Success) {
                    routeRepository.removeTaskFromProcessingList(ut)
                    emit(getExportedDataInfo())
                } else {
                    emit(getExportedDataInfo().apply {
                        isError = true
                        errorMessage = (apiResult as Result.Error).exception.message
                        loadStatus = ExitDialogView.LoadStatus.ERROR
                    })
                    finished = false
                }
            }

            undeliveredPhotos.forEach {
                val actualStatus =
                    photoRepository.allPhotosFlow.value.firstOrNull { actual -> actual.id == it.id }?.exportStatus

                if (actualStatus != ProcessingPhoto.ExportStatus.SENT) {
                    val result = photoRepository.uploadPhoto(it)
                    when (result) {
                        is Result.Success -> {
                            emit(getExportedDataInfo())
                        }
                        is Result.Error -> {
                            photoRepository.scheduleUploading(it)
                        }
                    }
                }
            }
        }

        emit(getExportedDataInfo().apply {
            loadStatus = ExitDialogView.LoadStatus.FINiSH
        })


        if (finished) {

            val loc = locationProvider.getCurrentLocation()

            if (loc == null) {
                FirebaseCrashlytics.getInstance().recordException(Exception("location is null"))
                FirebaseCrashlytics.getInstance().log("finishing route")
            }


            val result = finishRoute(
                FinishRouteData(
                    true, loc?.latitude ?: 0.0,
                    loc?.longitude ?: 0.0
                )
            )



            if (result is Result.Success) {
                emit(getExportedDataInfo().apply {
                    try {
                        reportBase.sendReport(routeId, this@RouteInteractor, true)
                    } catch (e: java.lang.Exception) {

                    }
                    isFinished = true
                    loadStatus = ExitDialogView.LoadStatus.FINiSH
                })
            } else if (result is Result.Error) {

                val undeliveredPhotos =
                    photoRepository.allPhotosFlow.value.filter { photo -> photo.routeId == routeId && photo.exportStatus != ProcessingPhoto.ExportStatus.SENT }

                val allowableError =
                    "Не все фото выгружены на сервер. Попробуйте еще раз. Если ошибка повторится - обратитесь к диспетчеру."

                try {
                    reportBase.sendReport(routeId, this@RouteInteractor, true)
                } catch (e: java.lang.Exception) {

                }
                emit(getExportedDataInfo().apply {
                    if (result.exception is ServerError) {
                        val error = result.exception.errorResponse?.message

                        if (error == allowableError && undeliveredPhotos.isEmpty()) {
                            isFinished = true
                            loadStatus = ExitDialogView.LoadStatus.FINiSH
                        } else {
                            errorMessage = error ?: "error"
                            isError = true
                            loadStatus =
                                if (undeliveredPhotos.isEmpty()) ExitDialogView.LoadStatus.FINiSH else ExitDialogView.LoadStatus.ERROR
                        }
                    }
                })

            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun sendAllInfoToServerOnConnectionAvailable() {
        if (ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.FILE)) {
            photoInteractor.uploadAllUndeliveredPhotos()
            routeRepository.fetchContainersHistory()
        }

        val routeId = routeRepository.startedRoute?.id
        if (routeId != null) {

            val localTaskCache = routeRepository.getCurrentTask()
            if (localTaskCache is Result.Success) routeRepository.sendCurrentTaskId(localTaskCache.data.id.toLong())
            // get undelivered tasks and send to server
            val undeliveredTasks = routeRepository.getUndeliveredTaskResults()
            if (!undeliveredTasks.isNullOrEmpty()) {
                for (ut in undeliveredTasks) {
                    val apiResult = routeRepository.processStandResults(routeId, listOf(ut))
                    if (apiResult is Result.Success) {
                        routeRepository.removeTaskFromProcessingList(ut)
                    }
                }
            }
        }
    }

    private fun getExportedDataInfo(): ExportedDataInfo {

        val taskCount: Int = routeRepository.getCurrentTasksAsCache()
            .filter {
                it.visitPoint?.pointType?.name != VisitPointType.Type.Parking
            }.size

        val allPhoto =
            photoRepository.allPhotosFlow.value.filter { photo -> photo.routeId == routeRepository.startedRoute?.id }

        val exportedPhoto =
            allPhoto.filter { photo -> photo.exportStatus == ProcessingPhoto.ExportStatus.SENT }

        var destroyPhotoCount = allPhoto.filter { photoRepository.checkPhotoCrash(it.photoPath) }

        destroyPhotoCount =
            destroyPhotoCount.filter { photoRepository.checkPhotoCrash(it.cachePath) }

        val exportedTaskCount = taskCount - routeRepository.getUndeliveredTaskResults().size

        return ExportedDataInfo(
            allPhoto.size,
            exportedPhoto.size,
            taskCount,
            exportedTaskCount,
            estimatedTime = calculationUnloadingTime(),
            destroyPhotoCount = destroyPhotoCount.size
        )
    }

    private fun calculationUnloadingTime(): Int {
        var estimatedTime = -1

        try {
            val upSpeed = ConnectivityUtils.getNetworkUpSpeed(context)

            val totalSizeAllUnsentPhotos =
                photoRepository.allPhotosFlow.value.filter { photo ->
                    photo.routeId == routeRepository.startedRoute?.id &&
                            photo.exportStatus != ProcessingPhoto.ExportStatus.SENT && File(
                        photo.photoPath
                    ).exists()
                }.map { (File(it.photoPath).length() / 1024).toString().toInt() }.sum()

            if (upSpeed > 0) estimatedTime = totalSizeAllUnsentPhotos / upSpeed

        } catch (e: Exception) {
            LogUtils.error(TAG, "Error message: ${e.message}")
        }

        return estimatedTime
    }

    suspend fun getPolygonsList(): Result<List<VisitPoint>> {
        val polygonsList = commonDataRepository.getVisitPoints()
        val startedRoute = routeRepository.getStartedRouteFromCache()

        if (polygonsList is Result.Success && startedRoute is Result.Success) {
            return Result.Success(polygonsList.data.filter { item ->
                startedRoute.data.unit.allowedPolygonIds.contains(
                    item.id
                )
            })
        } else if (polygonsList is Result.Error) {
            return polygonsList
        } else if (startedRoute is Result.Error) {
            return startedRoute
        }
        return Result.Error(Exception("Ошибка создания списка полигонов"))
    }

    suspend fun startToPolygon(polygonVisitPointId: String): Result<List<TaskExtended>> {
        val result = routeRepository.startToPolygon(polygonVisitPointId)

        if (result is Result.Error) {
            return result
        }

        delay(5000L)

        return loadTasksForCurrentRoute(
            routeRepository.startedRoute?.id ?: throw Exception("npe"),
            true,
            true
        )

    }

    suspend fun setArrival(taskId: Long) = routeRepository.setArrival(taskId)

    suspend fun onConnectionAvailable(): Flow<ExportedDataInfo> {

        if (startedRoute == null) {
            commonDataRepository.checkForUpdates({
                GlobalScope.launch(Dispatchers.Main) {
                    // TODO : проверить че за хуйня
//                    router.navigateTo(Screens.Updating)
                }
            }, {})
        } else {
            if (ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.FILE)) {
                commonDataRepository.checkForUpdates(
                    {
                        GlobalScope.launch(Dispatchers.Main) {
//                        router.navigateTo(Screens.Updating)
                        }
                    },
                    {},
                    ConnectivityUtils.syncAvailability(
                        context,
                        ConnectivityUtils.DataType.SECONDARY
                    )
                )
            }
        }

        gmWebSocket.startListen()
        if (isRouteStarted) {
            startedRoute?.let {
                routeRepository.sendHeartbeat(it.id)
            }
        }

        if (routeRepository.startedRoute != null && ConnectivityUtils.syncAvailability(context)) {
            sendAllInfoToServerOnConnectionAvailable()
        }

        return flow { emit(getExportedDataInfo()) }
    }

    suspend fun sendUndeliveredTasks() {
        val routeId = routeRepository.startedRoute?.id ?: return
        val undeliveredTasks = routeRepository.getUndeliveredTaskResults()
        if (!undeliveredTasks.isNullOrEmpty()) {
            for (ut in undeliveredTasks) {
                val apiResult = routeRepository.processStandResults(routeId, listOf(ut))
                if (apiResult is Result.Success) {
                    routeRepository.removeTaskFromProcessingList(ut)
                }
            }

        }
    }

    fun getAllTaskInDevise(): List<TaskExtended>? {
        return routeRepository.getAllTask()
    }

    fun getDeliveredTask(taskId: Long): TaskProcessingResult? {
        return routeRepository.getDeliveredTaskById(taskId)
    }

    fun getTaskResultById(taskId: Long): TaskProcessingResult? =
        routeRepository.getTaskResultById(taskId)

    fun getTaskProcessingResults(): List<TaskProcessingResult>? {
        return routeRepository.getTaskProcessingResults()
    }

    fun closeListen() = gmWebSocket.close()

    fun clearCache() = routeRepository.clearAllCache()

    //Слушатель  на изменёное сотояние фото грусчиков
    @JvmName("setPhotoLoader1")
    fun setPhotoLoader(photo: Boolean){
        photoLoader?.invoke(photo)
    }

    //Слушатель  на изменёное сотояние фото грусчиков
    fun getPhotoLoader(photo: (Boolean) -> Unit) {
        photoLoader = photo
    }
}
