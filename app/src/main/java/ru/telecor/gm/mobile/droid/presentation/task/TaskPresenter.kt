package ru.telecor.gm.mobile.droid.presentation.task

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.FIREBASE_TASK_ID_KEY
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.VisitPointType
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.request.FinishRouteData
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.entities.task.GeneralTaskModel
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.utils.dateTime
import ru.telecor.gm.mobile.droid.utils.millisecondsDate
import ru.terrakok.cicerone.Router
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.task
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class TaskPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: ResourceManager,
    private val router: Router,
    private val settingsPrefs: SettingsPrefs
) : BasePresenter<TaskView>() {

    var repetitionLimit = false

    private lateinit var localTaskCache: TaskExtended
    private lateinit var localTaskDraftProcessingResult: TaskDraftProcessingResult

    var taskId: Int = -1

    private fun isCurrent() = taskId == -1

    private var isMapReady = false

    private val tasksListUpdatedListener: (tasks: List<TaskExtended>) -> Unit = {
        val currentTask = routeInteractor.getCurrentTask()
        handleResult(currentTask, {
            if (isCurrent()) {
                GlobalScope.launch {
                    val taskDraftData = routeInteractor.getDraftByTaskID(it.data.id.toLong())
                    handleResult(taskDraftData, { tdRes ->
                        localTaskDraftProcessingResult = tdRes.data
                    }, {})
                }
                localTaskCache = it.data
                updateUI()
            }
        }, {
//            onPolygonDestinationDialogCancelled()
            // ToDo а страницу выбора грузчика
            handleError(it, rm)
        })
    }

    override fun attachView(view: TaskView?) {
        super.attachView(view)
        routeInteractor.addOnTasksUpdatedListener(tasksListUpdatedListener)
        initScreen()
        updateTime()
    }

    private fun updateTime() {
        val dateFormat = SimpleDateFormat("HH:mm")
        val time = Calendar.getInstance().time
        viewState.showCurrentTime(dateFormat.format(time))
    }

    fun updateBatteryLevel(string: String) {
        viewState.showBatteryLevel(string)
    }

    fun onSettingsClicked() {
        viewState.showSettingsMenu()
    }

    fun isVisibilityNext(boolean: Int) {
        settingsPrefs.visibilityNext = boolean
    }

    /**
     * Метод решает, какого рода задача и преобразует интерфейс в соответствии с этим.
     * Не путать с updateUI!
     */
    fun initScreen() {
        viewState.setLoadingState(true)
        launch {
            val current = routeInteractor.getCurrentTask()

            handleResult(current, {
                if (it.data.id == taskId) {
                    taskId = -1
                }
                FirebaseCrashlytics.getInstance().setCustomKey(FIREBASE_TASK_ID_KEY, it.data.id)
            }, {
                viewState.setLoadingState(false)
                handleError(it, rm)
            })
            val result = if (isCurrent()) {
                routeInteractor.getCurrentTask()
            } else {
                routeInteractor.getTaskById(taskId)
            }

            handleResult(result, {
                GlobalScope.launch {
                    val taskDraftData = routeInteractor.getDraftByTaskID(it.data.id.toLong())
                    handleResult(taskDraftData, { tdRes ->
                        localTaskDraftProcessingResult = tdRes.data
                    }, {})
                }
                localTaskCache = it.data
                viewState.setLoadingState(false)
                if (isCurrent() && localTaskCache.visitPoint?.pointType?.name == VisitPointType.Type.Recycling || localTaskCache.visitPoint?.pointType?.name == VisitPointType.Type.Portal) {
                    askThePolygonDestination()
                } else
                    updateUI()
            }, {
                viewState.setLoadingState(false)
//                onPolygonDestinationDialogCancelled()
                // ToDo а страницу выбора грузчика
                handleError(it, rm)
                clearUI()
            })
        }
    }

    fun askThePolygonDestination() {
        viewState.setLoadingState(true)
        launch {
            //viewState.showPolygonRequestLoadingDialog(true)
            val result = routeInteractor.getPolygonsList()
            //viewState.showPolygonRequestLoadingDialog(false)
            handleResult(
                result,
                {
                    viewState.setLoadingState(false)
                    viewState.showPolygonSelectionDialog(it.data)
                },
                {
                    viewState.setLoadingState(false)
                    viewState.showInternedNeeded()
                    handleError(it, rm)
                })
        }
    }

    fun onVisitPointSelected(visitPoint: VisitPoint) {
        viewState.showPolygonSelectionEnsureDialog(visitPoint)
    }

    fun onVisitPointEnsured(visitPoint: VisitPoint) {
        viewState.setLoadingState(true)
        launch {
            val res = routeInteractor.startToPolygon(visitPoint.id)
            handleResult(
                res,
                {
                    viewState.setLoadingState(false)
                    tryPolygonRequest()
                },
                {
                    handleError(it, rm)
                    viewState.setLoadingState(false)
                    viewState.showPolygonRequestRetryDialog()
                })
        }
    }

    fun onVisitPointEnsureCancelled() {
        askThePolygonDestination()
    }

    fun onPolygonDestinationDialogCancelled() {
        router.navigateTo(Screens.RouteStands)
    }

    fun tryPolygonRequest() {
        if (localTaskCache.visitPoint == null) {
            initScreen()
            return
        }
        viewState.setLoadingState(true)
        launch {
            val res = routeInteractor.startToPolygon(localTaskCache.visitPoint!!.id)
            handleResult(res, {
                viewState.setLoadingState(false)
                router.newRootScreen(Screens.Dumping)
            }, {
                viewState.setLoadingState(false)
                viewState.showPolygonRequestRetryDialog()
            })
        }
    }

    override fun detachView(view: TaskView?) {
        super.detachView(view)

        routeInteractor.removeTasksUpdatedListener(tasksListUpdatedListener)
    }

    private fun clearUI() {
        viewState.setContactNumber("")
        viewState.setCustomerInfo("")
        viewState.setAddress("")
        viewState.setLoadLevel("")
        viewState.setPreferredTime("")
        viewState.setTaskList(arrayListOf())
        viewState.setComment("")
        viewState.setPriority("")
    }

    private fun updateUI() {
        viewState.setClickable(true)
        launch {

            if (isCurrent() && localTaskCache.stand == null) {
                clearUI()
                viewState.setAddress(localTaskCache.visitPoint?.name.emptyIfNull())
                when (localTaskCache.visitPoint?.pointType?.name) {
                    VisitPointType.Type.Parking -> viewState.setLastTaskMode(true)
                    VisitPointType.Type.Empty -> {}
                    VisitPointType.Type.Fueling -> {}
                    else -> {}
                }
                localTaskCache.visitPoint?.let {
                    if (isMapReady) {
                        viewState.showMapLocation(
                            it.latitude,
                            it.longitude,
                            null,
                            it.evacuationPoint.geoJson,
                            it.evacuationPoint.routeToRoad
                        )
                    }
                }
                viewState.setLastTaskMode(true)
            } else {
                setActualInfo()
            }

            when {
                isCurrent() -> {
                    viewState.taskShowMode(TaskView.Mode.CURRENT)
                }
                localTaskCache.statusType != StatusType.NEW -> {
                    viewState.taskShowMode(TaskView.Mode.DONE)
                }
                else -> {
                    viewState.taskShowMode(TaskView.Mode.PREVIEW)
                }
            }
        }
    }

    private fun setActualInfo() {
        viewState.setRouteComment(localTaskCache.comment.emptyIfNull())
        viewState.setContactNumber(localTaskCache.contactPhone.emptyIfNull())
        viewState.setCustomerInfo(localTaskCache.taskItems.map { item -> item.customer.name }
            .distinct().sorted().toString())
        viewState.setAddress(localTaskCache.stand?.address ?: "")
        val ctList =
            localTaskCache.taskItems.mapNotNull { ti -> localTaskCache.stand?.containerGroups?.findLast { cG -> cG.containerType.id == ti.containerTypeId } }

        val supportedGarbageTypes =
            routeInteractor.startedRoute?.unit?.vehicle?.supportedGarbageTypes?.map { it.id }
        var trDraft: TaskDraftProcessingResult? = null
        if(::localTaskDraftProcessingResult.isInitialized){
            localTaskDraftProcessingResult.let { tD->
                if (tD.id == localTaskCache.id.toLong()) trDraft = tD
            }
        }
        val finalList = ctList.map {
            val supportedGarbageType =
                if (supportedGarbageTypes != null) it.garbageType.id in supportedGarbageTypes else true
            TaskItemPreviewData(
                it.containerType,
                it.garbageType,
                localTaskCache.containerAction,
                if (supportedGarbageType) localTaskCache.taskItems.filter { ti -> ti.containerTypeId == it.containerType.id }
                    .map { item -> item.planCount }.sum() else it.count,
                supportedGarbageType,
                null,
                localTaskCache.taskItems.filter { ti -> ti.containerTypeId == it.containerType.id },
                taskDraftData = trDraft
            )
        }.toMutableList()

        var l = GeneralTaskModel( finalList.sortedByDescending { it.supportedGarbageType.toString() },
        localTaskCache.taskItems.sortedByDescending { it.containerTypeId })

        //Добавил тоаст
        viewState.setTaskList(finalList.sortedByDescending {it.supportedGarbageType.toString()})

        viewState.setPreferredTime(
            SimpleDateFormat(
                "E HH:mm",
                Locale("ru")
            ).format(Date(localTaskCache.preferredTimeStart))
                .capitalize() + " - " + SimpleDateFormat(
                "E HH:mm", Locale("ru")
            ).format(Date(localTaskCache.preferredTimeEnd)).capitalize()
        )
        viewState.setComment(localTaskCache.comment ?: "")
        viewState.setPriority(localTaskCache.priority?.name ?: "")
        viewState.setPlanTimeSten(millisecondsDate(localTaskCache.planTimeStart))

        localTaskCache.stand?.let {
            if (isMapReady) {
                viewState.showMapLocation(
                    it.latitude,
                    it.longitude,
                    localTaskCache.stand?.geoJson,
                    localTaskCache.stand?.evacuationPoint?.geoJson,
                    localTaskCache.stand?.evacuationPoint?.routeToRoad
                )
            }
        }
    }

    fun navigatorButtonClicked() {
        if(::localTaskCache.isInitialized){
            localTaskCache.stand?.let {
                viewState.startNavigationApplication(
                    it.latitude,
                    it.longitude
                )
            }
            localTaskCache.visitPoint?.let {

                viewState.startNavigationApplication(
                    it.latitude,
                    it.longitude
                )
            }
        }
    }

    fun routeButtonClicked() {
        router.navigateTo(Screens.RouteStands)
    }

    fun setAsCurrentButtonClicked() {
        viewState.setLoadingState(true)

        launch {
            routeInteractor.selectTaskAsNext(taskId.toLong())
            taskId = -1
            viewState.setLoadingState(false)
            updateUI()
        }
    }

    fun cameButtonClicked() {
        if (::localTaskCache.isInitialized){
            localTaskCache.stand?.let {
                viewState.showLoadDialog(it.address)
            }
            localTaskCache.visitPoint?.let {
                if (it.pointType.name == VisitPointType.Type.Parking){
                    viewState.showFinishDialog()
                }
            }
            //При вызове сохроняю время
            launch {
                handleResult(routeInteractor.setArrival(localTaskCache.id.toLong()),{},{
                    routeInteractor.processingArrivalTime = Date().time
                })
            }
        }
    }


    @Suppress("DeferredResultUnused")
    fun onDialogConfirmed() {
        // NOTE: waiting for update of the method
//        async { routeInteractor.setArrival(localTaskCache.id.toLong()) }
//        if (::localTaskCache.isInitialized) {
//            launch {
//                handleResult(routeInteractor.setArrival(localTaskCache.id.toLong()),{},{
//                    routeInteractor.processingArrivalTime = Date().time
//                })
//            }
//        }
        router.navigateTo(Screens.GarbageLoad)
    }

    private fun reorderToGarbageLoad() {

    }

    fun onDialogProblemSelected() {
//        if (::localTaskCache.isInitialized) {
//            launch { routeInteractor.setArrival(localTaskCache.id.toLong()) }
//        }
        router.navigateTo(Screens.TaskTrouble)
    }

    fun onRouteFinishButtonClicked(finishRouteData: FinishRouteData) {
        viewState.showFinishDialog()
    }

    fun onMapReady() {
        isMapReady = true
    }

    fun onMapFullScreenButtonPressed() {
        localTaskCache.stand?.let {
            viewState.openFullScreenMap(
                it.latitude,
                it.longitude,
                localTaskCache.stand?.geoJson,
                localTaskCache.stand?.evacuationPoint?.geoJson,
                localTaskCache.stand?.evacuationPoint?.routeToRoad
            )
        }
    }

    fun onQrCodeScannerButtonClicked() {
        router.navigateTo(Screens.QrCodeScanner)
    }

    fun onBackPressed() {
        router.replaceScreen(Screens.RouteStart)
    }
}
