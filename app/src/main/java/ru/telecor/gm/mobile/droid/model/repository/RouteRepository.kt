package ru.telecor.gm.mobile.droid.model.repository

import android.annotation.SuppressLint
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.request.*
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.entities.task.ContainerAction
import ru.telecor.gm.mobile.droid.model.data.db.dao.*
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApiLongRequest
import ru.telecor.gm.mobile.droid.model.system.LocationProvider
import ru.telecor.gm.mobile.droid.utils.GsonUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.repository
 *
 * Repository with data of routes.
 *
 * Created by Artem Skopincev (aka sharpyx) 03.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class RouteRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val commonDataRepository: CommonDataRepository,
    private val api: TruckCrewApi,
    private val apiLongRequest: TruckCrewApiLongRequest,
    private val locationProvider: LocationProvider,
    private val tourDao: TourDao,
    private val taskExtendedDao: TaskExtendedDao,
    private val taskDraftProcessingResultDao: TaskDraftProcessingResultDao,
    private val taskProcessingResultDao: TaskProcessingResultDao,
    private val taskRelationsDaoDao: TaskRelationsDao,
  //  private val photoRepository: PhotoRepository
) : BaseRepository() {

    private var startedRouteCache: RouteInfo? = null
    private var gsonUtils = GsonUtils()

    private var currentTaskCache: TaskExtended? = null
        set(value) {
            currentTaskCache?.isCurrent = false
            field = value
            currentTaskCache?.isCurrent = true
        }
    private var currentRouteTasksCache: MutableList<TaskExtended>? = null
    var carryContainersHistory: List<CarryContainerHistoryItem>? = null
        set(value) {
            field = value
            tourDao.setCarryContainersHistory(value)
        }
        get() {
            if (field == null) {
                field = tourDao.getCarryContainersHistory()
            }
            return field ?: listOf()
        }

    var currentCarryContainers: List<CarryContainer>? = null
        set(value) {
            field = value
            tourDao.setCarryContainers(value)
            carryContainersListener?.invoke(value ?: listOf())
        }
        get() {
            if (field == null) {
                field = tourDao.getCarryContainers()
            }
            return field ?: listOf()
        }

    var carryContainersListener: ((List<CarryContainer>) -> Unit)? = null

    fun addCarryContainer(carryContainer: CarryContainer) {
        currentCarryContainers =
            currentCarryContainers?.plus(carryContainer) ?: listOf(carryContainer)

        addContainerHistoryItem(
            carryContainer,
            ContainerHistoryActionType.ADD
        )
    }

    fun removeCarryContainer(carryContainer: CarryContainer) {
        currentCarryContainers =
            currentCarryContainers?.minus(carryContainer)

        addContainerHistoryItem(
            carryContainer,
            ContainerHistoryActionType.REMOVE
        )
    }

    private fun addContainerHistoryItem(
        carryContainer: CarryContainer,
        actionType: ContainerHistoryActionType
    ) {
        val location = locationProvider.getCurrentLocation()

        val cchi = CarryContainerHistoryItem(
            carryContainer,
            actionType,
            Date().time,
            location?.latitude ?: 0.0,
            location?.longitude ?: 0.0,
        )
        carryContainersHistory = carryContainersHistory?.plus(cchi) ?: listOf(cchi)
        GlobalScope.launch {
            fetchContainersHistory()
        }
    }

    suspend fun fetchContainersHistory() {
        carryContainersHistory?.let {
            val undeliveredItems =
                it.filter { item -> !item.isSent }
            val mutableList = it.toMutableList()
            for (item in undeliveredItems) {
                val sendingItem = CarryContainerHistoryItemSendable.create(item)
                val result =
                    safeApiCall {
                        api.pushHistoryItem(
                            startedRoute?.id ?: throw Exception("route is null"), sendingItem
                        )
                    }
                if (result is Result.Success) {
                    val index = mutableList.indexOf(item)
                    mutableList.removeAt(index)
                    mutableList.add(index, item.copy(isSent = true))
                    carryContainersHistory = mutableList
                } else {
                    Timber.e((result as Result.Error).exception)
                    return
                }
            }
        }

        val historyListResult = safeApiCall {
            api.getContainersHistory(
                startedRoute?.id ?: throw Exception("route is null")
            )
        }

        val containersListResult = safeApiCall {
            api.getCurrentContainers(
                startedRoute?.id ?: throw Exception("route is null")
            )
        }

        if (historyListResult is Result.Success && containersListResult is Result.Success) {
            currentCarryContainers = containersListResult.data
            carryContainersHistory =
                historyListResult.data.map { item -> CarryContainerHistoryItem.create(item) }
        }
    }

    suspend fun getPhotoRequest(id: String) = safeApiCall {
        api.getPhotoRequest(id)
    }


    val startedRoute: RouteInfo?
        get() {
            if (startedRouteCache == null) {
                startedRouteCache = tourDao.getCurrentRoute()
            }
            return startedRouteCache
        }

    //Многоходов очка
    suspend fun getStartedRoute(
        fromServer: Boolean = false,
        syncAval: Boolean,
        id: Long
    ): Result<RouteInfo> {
        return if (syncAval) {
            if (fromServer) {
                getStartedRouteFromServer(id)
            } else {
                val offlineRes = getStartedRouteFromCache()
                if (offlineRes is Result.Error) {
                    getStartedRouteFromServer(id)
                } else offlineRes
            }
        } else {
            getStartedRouteFromCache()
        }
    }

    fun getStartedRouteFromCache(): Result<RouteInfo> {
        val result = startedRoute ?: return Result.Error(Exception("Маршрут не установлен"))
        return Result.Success(result)
    }

    //fixme: На беке должен появиться метод, который отвечает за получение данных о маршруте без команды его начала/продолжения
    suspend fun getStartedRouteFromServer(id: Long = 0) =
        safeApiCall {
//            val route = api.continueRoute(startedRoute?.id ?: 0)
            val route = api.continueRoute(id)

            startedRouteCache = route
            tourDao.setCurrentRoute(startedRouteCache)

            route
        }

    suspend fun getTasksForRoute(routeId: Long): List<TaskExtended>? {
        if (currentRouteTasksCache != null) return currentRouteTasksCache

        currentRouteTasksCache = taskExtendedDao.getAll()?.toMutableList()
        if (currentRouteTasksCache == null) {
            val l = loadTasksForRoute(routeId, true)
            if (l is Result.Success) {
                currentRouteTasksCache = l.data.toMutableList()
            }
        }

        return currentRouteTasksCache
    }

    fun getCurrentTasksAsCache(): List<TaskExtended> {
        return currentRouteTasksCache ?: taskExtendedDao.getAll() ?: listOf()
    }

    fun getAllTask(): List<TaskExtended> {
        return taskExtendedDao.getAll() ?: listOf()
    }

    fun getCurrentTaskSimply(): TaskExtended? {
        return currentTaskCache
    }

    fun getCurrentTask(): Result<TaskExtended> {
        if (currentTaskCache == null) {
            recalculateCurrentTask()
        }
        currentTaskCache?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Текущее задание не установлено"))
    }

    suspend fun getDraftByTaskID(taskId: Long): Result<TaskDraftProcessingResult> {
        val draftInfo = taskDraftProcessingResultDao.getById(taskId)
        return if (draftInfo != null)
            Result.Success(draftInfo)
        else Result.Error(java.lang.Exception("Entity not found"))
    }


    suspend fun getFlightTicketModel(id: Long?): Result<List<GetListCouponsModel>> {
        id?.let {
            if (it > 0) {
                return safeApiCall {
                    return@safeApiCall api.flightTicket(it)
                }
            }
        }
        return Result.Error(Exception("Текущее задание не установлено"))
    }

    fun getRoutes(): Result<List<RouteInfo>> {
        val routes = authRepository.tourInfo?.delayedRoutes
            ?: return Result.Error(Exception("Маршруты не найдены"))
        return Result.Success(routes)
    }

//    suspend fun autoResultCreator() {
//        val defaultProcessingResult = getTaskResultById(665692)
//        val photo = photoRepository.allPhotosFlow.value.firstOrNull() { it.taskId.toInt() == 665692 }
//        if (defaultProcessingResult != null) {
//            currentRouteTasksCache?.let { crt ->
//                crt.filter { t -> t.statusType == StatusType.NEW && t.stand != null }.map { ctt ->
//                    defaultProcessingResult.standResults?.let { dsr ->
//                        val photoCrateDate = Date().time.toString()
//                        val sr = dsr.map { srd ->
//                            val photosRes = srd.photos?.map { pt ->
//                                pt.copy(
//                                    filename = "${defaultProcessingResult.routeId}${File.pathSeparator}${ctt.id.toLong()}${File.pathSeparator}TTR_${photoCrateDate}.jpg",
//                                    time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(
//                                        photoCrateDate.toLong()
//                                    )
//                                )
//                            }
//                            srd.copy(id = ctt.id.toLong(), photos = photosRes)
//                        }
//
//                        val res =
//                            defaultProcessingResult.copy(id = ctt.id.toLong(), standResults = sr)
//
//                        val apiResult = processStandResults(res.routeId, listOf(res))
//                        if (apiResult is Result.Success) {
//                            taskExtendedDao.update(
//                                ctt.copy(
//                                    statusType = StatusType.FAIL,
//                                    changeTime = dsr.first().changeTime
//                                )
//                            )
//                           // removeTaskFromProcessingListw(res)
//                            photo?.let {
//                                photoRepository.addPhoto(
//                                    routeId = res.routeId,
//                                    taskId = ctt.id.toLong(),
//                                    photoType = photo.photoType,
//                                    latitude = photo.latitude,
//                                    longitude = photo.longitude,
//                                    file = File(photo.cachePath),
//                                    timeStamp = photoCrateDate,
//                                    isExportable = true,
//                                    usResize = false
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    suspend fun startRoute(id: Long) = safeApiCall(Dispatchers.IO) {
        val newRouteInfo = api.startRoute(id)
        // save to cache
        startedRouteCache = newRouteInfo
        tourDao.setCurrentRoute(startedRouteCache)

        newRouteInfo
    }

    suspend fun continueRoute(id: Long) = safeApiCall {
        val newRouteInfo = api.continueRoute(id)

        //save to cache
        startedRouteCache = newRouteInfo
        tourDao.setCurrentRoute(startedRouteCache)

        newRouteInfo
    }

    suspend fun removeTask(task: TaskExtended) {
        if (task.order < 1000) {
            taskExtendedDao.delete(task)
            if (currentTaskCache == task) currentTaskCache = null
            recalculateCurrentTask()
        }
    }

    // Need to delete
    suspend fun getRouteStands(routeId: Long) = safeApiCall(Dispatchers.IO) {
        return@safeApiCall api.getRouteStands(routeId)
    }

    suspend fun finishRoute(finishRouteData: FinishRouteData) = safeApiCall(Dispatchers.IO) {
        return@safeApiCall api.finishRoute(startedRoute?.id ?: 0, finishRouteData)
    }

    suspend fun polygonCancel(routeId: Int) = safeApiCall(Dispatchers.IO) {
        return@safeApiCall api.polygonCancel(routeId)
    }

    suspend fun prepareFinishRoute() = safeApiCall(Dispatchers.IO) {
        return@safeApiCall api.prepareFinishRoute(startedRoute?.id ?: 0)
    }

    suspend fun setLoader(num: Int, loaderInfo: LoaderInfo?) = safeApiCall(Dispatchers.IO) {
        return@safeApiCall api.setPorter(
            startedRoute?.id ?: 0,
            PorterData.create(num, loaderInfo)
        )
    }

    fun getTaskById(id: Int): Result<TaskExtended> {
        val task = currentRouteTasksCache?.findLast { it.id == id }
        return if (task != null) {
            Result.Success(task)
        } else {
            Result.Error(Exception("task not funded"))
        }
    }

    suspend fun updateRouteByMessage(
        msg: WebSocketMsg
    ): MutableList<TaskExtended>? {


        if (gsonUtils.isJSONValid(msg.command.data) && currentRouteTasksCache != null) {

            val route = Gson().fromJson(msg.command.data, RouteDataMsg::class.java)
            val visitPointsResult = commonDataRepository.getVisitPoints()
            val extendedTasks: MutableList<TaskExtended>
            var visitPoints: List<VisitPoint>? = null
            var stands: List<Stand>? = null
            var update = true

            if (visitPointsResult is Result.Success) {
                visitPoints = visitPointsResult.data
            }

            val defaultCA = ContainerAction(
                "Забрать с заменой",
                "REPLACE"
            )

            route.fullList2?.let { tasks ->

                extendedTasks = tasks.mapNotNull { task ->

                    if (visitPoints?.findLast { it.id == task.visitPointId } != null) return@mapNotNull null

                    val existingTask =
                        currentRouteTasksCache?.firstOrNull { extendedTasks -> extendedTasks.id == task.id }
                            ?: taskExtendedDao.getById(task.id)

                    if (existingTask == null) {
                        if (stands == null) {
                            val standsResult = commonDataRepository.getStands(route.routeId, update)
                            if (standsResult is Result.Success) {
                                stands = standsResult.data
                                update = false
                            } else {
                                return@mapNotNull null
                            }
                        }

                        val stand = stands?.findLast { it.id == task.standId }
                        if (stand == null && task.visitPointId == null) return@mapNotNull null

                        LogUtils.error(javaClass.simpleName, task.toString())

                        TaskExtended(
                            id = task.id,
                            planTimeStart = dateToTS(task.planTimeStart),
                            changeTime = dateToTS(task.changeTime),
                            stand = stand,
                            visitPoint = visitPoints?.findLast { it.id == task.visitPointId },
                            taskItems = task.taskItems,
                            statusType = StatusType.NEW,
                            comment = task.comment,
                            shippingByPiece = task.shippingByPiece,
                            containerAction = task.containerAction ?: defaultCA,
                            preferredTimeStart = dateToTS(task.preferredTimeStart),
                            preferredTimeEnd = dateToTS(task.preferredTimeEnd),
                            priority = task.priority
                        )
                    } else existingTask.copy(isCurrent = false)


                }.toMutableList()

                if (extendedTasks.size > 0) {
                    val idsInProcessing =
                        taskProcessingResultDao.getAll()
                            ?.mapNotNull { it.standResults?.first()?.id?.toInt() }
                            ?: listOf()

                    val oldRouteTasksIds = currentRouteTasksCache?.map { it.id } ?: listOf()
                    val deletedTasks =
                        currentRouteTasksCache?.filter { it.id !in extendedTasks.map { new -> new.id } && it.visitPoint == null && it.id !in idsInProcessing }
                    val newTasks =
                        extendedTasks.filter { it.id !in oldRouteTasksIds && it.id !in idsInProcessing }

                    CoroutineScope(Dispatchers.IO).launch {

                        val lastPolygon = currentRouteTasksCache?.findLast { t ->
                            isPolygon(t.visitPoint)
                        }

                        extendedTasks.let { allTask ->
                            allTask.map { exsTask ->
                                val oldTask = currentRouteTasksCache?.find { it.id == exsTask.id }
                                oldTask?.let { ot ->
                                    if (ot.changeTime != exsTask.changeTime)
                                        taskExtendedDao.update(oldTask.copy(changeTime = exsTask.changeTime))
                                }
                            }
                        }

                        deletedTasks?.let { taskExtendedDao.deleteAll(it) }

                        var order =
                            currentRouteTasksCache?.findLast { it.id != lastPolygon?.id && it.visitPoint?.pointType?.name != VisitPointType.Type.Parking }?.order
                                ?: extendedTasks.size.toDouble()

                        newTasks?.let { nTasks ->
                            nTasks.map {
                                order++
                                taskExtendedDao.insert(it.copy(order = order))
                            }
                        }
                    }
                    currentRouteTasksCache = taskExtendedDao.getAll()?.toMutableList()
                    recalculateCurrentTask()
                }
            }
        }
        return currentRouteTasksCache
    }

    fun getTaskCountDb() = taskExtendedDao.getRowCount()

    @SuppressLint("SimpleDateFormat")
    fun dateToTS(date: String): Long =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date).time

    suspend fun loadTasksForRoute(
        routeId: Long,
        refresh: Boolean = false,
        firstRecyc: Boolean = false
    ): Result<List<TaskExtended>> = safeApiCall {

        if (!refresh) {
            val tt = taskExtendedDao.getAll()
            tt?.let { list ->
                currentRouteTasksCache = list.toMutableList()
                recalculateCurrentTask()
                return@safeApiCall list
            }
        }

        val processingResults = taskProcessingResultDao.getAll()
        val idsInProcessing =
            taskProcessingResultDao.getAll()?.mapNotNull { it.standResults?.first()?.id?.toInt() }
                ?: listOf()


        val tasks = apiLongRequest.getRouteTasks(routeId)
        val stands = apiLongRequest.getRouteStands(routeId)
        val visitPointsResult = commonDataRepository.getVisitPoints()
        val extendedTasks: MutableList<TaskExtended>
        var visitPoints: List<VisitPoint>? = null
        if (visitPointsResult is Result.Success) {
            visitPoints = visitPointsResult.data
        }
        var i = 0
        val lastPolygon = tasks.findLast { t ->
            isPolygon(visitPoints?.findLast { visPoint -> visPoint.id == t.visitPointId })
        }

        val customPolygon = if (firstRecyc)
            tasks.find { t ->
                isPolygon(visitPoints?.findLast { visPoint -> visPoint.id == t.visitPointId }) &&
                        t.id != lastPolygon?.id && t.statusType == StatusType.NEW
            }
        else null

        extendedTasks = tasks.mapNotNull { task ->
            i++
            val visitPoint = visitPoints?.findLast { it.id == task.visitPointId }

            val orderNumber =
                if (task.id == lastPolygon?.id || visitPoint?.pointType?.name == VisitPointType.Type.Parking) i + 1000 else i

            if (isPolygon(visitPoint) && task.statusType == StatusType.NEW) {
                if (lastPolygon?.id != task.id && customPolygon?.id != task.id) {
                    return@mapNotNull null
                }
            }
            val existTask = currentRouteTasksCache?.firstOrNull { exT -> exT.id == task.id }
                ?: taskExtendedDao.getById(task.id)

            val taskStatus = when {
                existTask?.statusType != StatusType.NEW -> existTask?.statusType
                task.statusType != StatusType.NEW -> task.statusType
                task.id in idsInProcessing -> {
                    processingResults?.firstOrNull { r -> r.standResults?.first()?.id == task.id.toLong() }?.statusType?.name
                        ?: StatusType.NEW
                }
                else -> StatusType.NEW
            }

            TaskExtended(
                task.id,
                task.planTimeStart,
                task.changeTime,
                stands.findLast { it.id == task.standId },
                visitPoint,
                task.taskItems,
                taskStatus ?: task.statusType,
                task.comment,
                task.contactPhone,
                task.shippingByPiece,
                task.containerAction,
                task.preferredTimeStart,
                task.preferredTimeEnd,
                task.priority,
                order = orderNumber.toDouble()
            )


        }.toMutableList()

        if (extendedTasks.size > 0) {

            val oldRouteTasksIds = currentRouteTasksCache?.map { it.id } ?: listOf()

            val newTasks =
                extendedTasks.filter { it.id !in oldRouteTasksIds && it.id !in idsInProcessing }

            val deletedTasks =
                currentRouteTasksCache?.filter { it.id !in extendedTasks.map { new -> new.id } && it.id !in idsInProcessing }

            currentRouteTasksCache = extendedTasks

            CoroutineScope(Dispatchers.IO).launch {
                extendedTasks.let { allTask ->
                    allTask.map { exsTask ->
                        val oldTask = currentRouteTasksCache?.find { it.id == exsTask.id }
                        oldTask?.let { ot ->
                            if (ot.changeTime != exsTask.changeTime)
                                taskExtendedDao.update(oldTask.copy(changeTime = exsTask.changeTime))
                        }
                    }
                }
                deletedTasks?.let { taskExtendedDao.deleteAll(it) }
                newTasks?.let { taskExtendedDao.insertAll(newTasks) }
                extendedTasks.filter { it.id in oldRouteTasksIds }.map { extask ->
                    val existTask = taskExtendedDao.getById(extask.id)
                    if (existTask != null) {
                        if (
                            existTask.order != extask.order && currentTaskCache?.id != extask.id
                        ) {
                            taskExtendedDao.update(existTask.copy(order = extask.order))
                        }
                    }
                }
            }

            if (extendedTasks.size > 0) {
                recalculateCurrentTask()
            }

        }

        return@safeApiCall currentRouteTasksCache ?: extendedTasks
    }

    private fun isPolygon(visitPoint: VisitPoint?): Boolean =
        visitPoint?.pointType?.name == VisitPointType.Type.Recycling || visitPoint?.pointType?.name == VisitPointType.Type.Portal

    fun setStatusAndNextTask(task: TaskExtended, statusType: StatusType) {
        val taskInCurrentCache =
            currentRouteTasksCache?.firstOrNull() { item -> item.id == task.id }

        CoroutineScope(Dispatchers.IO).launch {
            if (taskInCurrentCache != null) {
                taskExtendedDao.update(
                    taskInCurrentCache.copy(
                        statusType = statusType,
                        isCurrent = false,
                        changeTime = System.currentTimeMillis()
                    )
                )
            }
        }

        recalculateCurrentTask()
    }

    fun rCulateCurrentTask() = recalculateCurrentTask()


    fun getRecalculateType(): OperationType {

        val visitPoint = currentTaskCache?.visitPoint?.pointType?.name

        val newTasks =
            currentRouteTasksCache?.firstOrNull { it.statusType == StatusType.NEW && it.stand != null } != null

        val newLandfill = currentRouteTasksCache?.firstOrNull {
            it.visitPoint?.pointType?.name in arrayOf(
                VisitPointType.Type.Recycling,
                VisitPointType.Type.Portal
            ) && it.statusType == StatusType.NEW
        } != null

        return when {
            visitPoint == VisitPointType.Type.Parking && !newTasks -> OperationType.FREEZE
            !newLandfill -> OperationType.REFORM
            else -> OperationType.UPDATE
        }
    }


    private fun recalculateCurrentTask() {

        if (getRecalculateType() == OperationType.FREEZE)
            return

        val idsInProcessing =
            taskProcessingResultDao.getAll()?.mapNotNull { it.standResults?.first()?.id?.toInt() }
                ?: listOf()

        currentRouteTasksCache?.let {
            currentTaskCache = it.firstOrNull() {
                taskExtendedDao.updateIsCurrent()
                it.statusType == StatusType.NEW && it.id !in idsInProcessing
            }
        }
    }

    suspend fun reorderTasks(taskId: Long) = safeApiCall(Dispatchers.IO) {
        var task = currentRouteTasksCache?.find { it.id.toLong() == taskId }

        if (task != null) {
            currentRouteTasksCache?.remove(task)
            val curTask = currentTaskCache
            val currentIndex = currentRouteTasksCache?.indexOf(currentTaskCache) ?: 0
            task = task.copy(order = (currentTaskCache?.order ?: 0.0) - 0.01)
            currentRouteTasksCache?.add(currentIndex, task)
            if (curTask != null) {
                currentRouteTasksCache =
                    currentRouteTasksCache?.map {
                        if (it.id == curTask.id) it.copy(
                            order = it.order + 0.01,
                            isCurrent = false
                        ) else it
                    }
                        ?.toMutableList()
            }
            currentTaskCache = task
            currentRouteTasksCache?.let { taskExtendedDao.updateAll(it) }
        }
        return@safeApiCall api.reorderRoute(
            startedRoute?.id ?: -1,
            StandReorderData(nextPointId = taskId)
        )
    }

    suspend fun sendCurrentTaskId(taskId: Long) = GlobalScope.launch {
        safeApiCall {
            api.reorderRoute(
                startedRoute?.id ?: -1,
                StandReorderData(nextPointId = taskId)
            )
        }
    }

    // TODO: 14.07.2022 новая логика 
    suspend fun processStandResults(routeId: Long, results: List<TaskProcessingResult>): Result<ResponseBody> {
        var resultResult: Result<ResponseBody>? = null
        results.map { result ->
            val firstStandResult = result.standResults?.firstOrNull()
               if (firstStandResult != null){
                 if (setArrival(result.id, firstStandResult.arrivalTime) is Result.Success){
                     resultResult = safeApiCall {
                        api.pushOfflineResult(routeId, results)
                    }
                }
            } else {
                   resultResult = safeApiCall {
                    api.pushOfflineResult(routeId, results)
                }
            }
        }
        return  resultResult ?:  Result.Error(Exception("Низкая скорость интернета"))
    }


    fun addTaskResultToProcessingList(taskResult: TaskProcessingResult) =
        taskProcessingResultDao.insert(taskResult)

    @DelicateCoroutinesApi
    fun addTaskResultToDraftProcessingList(taskResult: TaskDraftProcessingResult) =
        GlobalScope.launch {
            taskDraftProcessingResultDao.insert(taskResult)
        }

    fun removeTaskFromProcessingList(taskResult: TaskProcessingResult) =
        GlobalScope.launch {
            taskProcessingResultDao.update(taskResult.copy(processingStatus = TaskProcessingResult.ProcessingStatus.DELIVERED))
        }

//    fun removeTaskFromProcessingListw(taskResult: TaskProcessingResult) =
//        GlobalScope.launch {
//            taskProcessingResultDao.insert(taskResult.copy(processingStatus = TaskProcessingResult.ProcessingStatus.DELIVERED))
//        }


    fun commitDeliveredTask(taskResult: TaskProcessingResult) =
        taskProcessingResultDao.insert(taskResult.copy(processingStatus = TaskProcessingResult.ProcessingStatus.DELIVERED))

    fun getDeliveredTaskById(id: Long): TaskProcessingResult? =
        taskProcessingResultDao.getByIdAndProcessingStatus(
            id,
            TaskProcessingResult.ProcessingStatus.DELIVERED
        )

    fun getTaskResultById(id: Long): TaskProcessingResult? = taskProcessingResultDao.getById(id)

    fun clearTaskProcessingList() =
        taskProcessingResultDao.updateAllProcessingStatus(TaskProcessingResult.ProcessingStatus.DELIVERED)

    fun getUndeliveredTaskResults(): List<TaskProcessingResult> =
        taskProcessingResultDao.getAllByStatus(TaskProcessingResult.ProcessingStatus.PROCESSING)

    fun getTaskProcessingResults(): List<TaskProcessingResult>? =
        taskProcessingResultDao.getAll()

    suspend fun startToPolygon(polygonVisitPointId: String) = safeApiCall {
        return@safeApiCall api.polygon(
            startedRouteCache?.id ?: -1,
            PolygonRequest(polygonVisitPointId)
        )
    }

    /**
     * Clear all cache: memory, prefs, db
     */
    fun clearAllCache() {
        startedRouteCache = null

        currentRouteTasksCache = null
        currentTaskCache = null
        currentCarryContainers = null
        carryContainersHistory = null
        GlobalScope.launch {
            taskProcessingResultDao.cleanTable()
            taskDraftProcessingResultDao.cleanTable()
            taskExtendedDao.cleanTable()
        }

    }

    suspend fun getNearStands(lat: Double, lon: Double) =
        safeApiCall { api.getNearestStands(PositionQuery(lat, lon)) }

    suspend fun setArrival(taskId: Long, time: Long?= null) = safeApiCall {
        return@safeApiCall api.arrival(ArrivalRequest(taskId, time ?: Date().time, true))
    }

    suspend fun sendHeartbeat(routeId: Long) = safeApiCall {
        return@safeApiCall api.heartbeat(routeId)
    }

    suspend fun getTasksWithRelations() = taskRelationsDaoDao.getAll()
}

enum class OperationType {
    FREEZE,
    UPDATE,
    REFORM
}
