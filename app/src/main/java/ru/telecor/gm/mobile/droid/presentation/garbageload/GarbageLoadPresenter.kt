package ru.telecor.gm.mobile.droid.presentation.garbageload

import android.location.Location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatus
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatusType
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.task.TaskItem
import ru.telecor.gm.mobile.droid.entities.task.TaskItemExtended
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.presentation.garbageload.util.GarbageLoadScreenState
import ru.terrakok.cicerone.Router
import ru.telecor.gm.mobile.droid.entities.db.PhotoProcessingForApi
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import android.R.array
import android.graphics.PointF.length


/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.garbageload
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 20.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class GarbageLoadPresenter @Inject constructor(
    val routeInteractor: RouteInteractor,
    private val commonDataRepository: CommonDataRepository,
    private val rm: IResourceManager,
    private val settingsPrefs: SettingsPrefs,
    val router: Router,
    private val photoInteractor: PhotoInteractor,
    private val dataStorageManager: DataStorageManager
) : BasePresenter<GarbageLoadView>() {

    private lateinit var localTaskCache: TaskExtended
    var isCheck: Boolean = false
    private lateinit var localTaskDraftProcessingResult: TaskDraftProcessingResult
    private var localFailureReasonsCache: List<ContainerFailureReason> = arrayListOf()
    private lateinit var localRouteCache: RouteInfo
    private lateinit var localLoadLevelsCache: List<ContainerLoadLevel>
    private val list: ArrayList<GarbagePhotoModel> = arrayListOf()

    val TAG = javaClass.simpleName
    private var containerStatuses: MutableList<ContainerStatus> = mutableListOf()
    var pickupTask: TaskItem? = null

    //Created in process of work
    var photoType: PhotoType = PhotoType.LOAD_TROUBLE
    var photoLocation: Location? = null
    var photoFile: File? = null
    var lastDonePhoto: ProcessingPhoto? = null
    private var valueInt = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshLocalData()

        //Заполнение адаптера фтоографиями
        for (modelType in 1..PhotoType.values().size) {
            launch {
                val type = PhotoType.values()[modelType - 1]
                photoInteractor.getTaskPhotosFlow(
                    localRouteCache.id,
                    localTaskCache.id.toLong(),
                    type
                ).collect { it ->
                    if (it.isNotEmpty()) {
                        viewState.showPhoto(
                            getSortingPhotos(
                                it as ArrayList<ProcessingPhoto>,
                                type
                            )
                        )
                    } else {
                        if (type == PhotoType.LOAD_TROUBLE) {
                            if (it.isNotEmpty()) {
                                viewState.showPhoto(
                                    getSortingPhotos(
                                        it as ArrayList<ProcessingPhoto>,
                                        type
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun attachView(view: GarbageLoadView?) {
        super.attachView(view)
        updateUI()
        if (::localTaskDraftProcessingResult.isInitialized)
            setDraftDataToClean(localTaskDraftProcessingResult)
    }

    private fun refreshLocalData() {
        viewState.setLoadingState(true)
        fetchCurrentTask()
        fetchStartedRoute()
        fetchContainersLevelsList()
        launch {
            fetchTroubleReasons()
            updateUI()
            viewState.setLoadingState(false)
        }
    }

    fun isVisibilityNext(boolean: Int) {
        settingsPrefs.visibilityNext = boolean
    }

    //Сортировка и добовление фото
    private fun getSortingPhotos(
        item: ArrayList<ProcessingPhoto>,
        modelType: PhotoType
    ): ArrayList<GarbagePhotoModel> {
        val getGarbagePhoto = list.firstOrNull { garbage -> garbage.type == modelType.toString() }
        if (getGarbagePhoto == null) {
            list.add(GarbagePhotoModel(modelType.toString(), item))
        } else {
            val type =
                list.indexOfFirst { type -> type.type == modelType.toString() && type.item.size != item.size }
            if (type != -1) {
                list.removeAt(type)
                list.addAll(type, listOf(GarbagePhotoModel(modelType.toString(), item)))
            }
        }
        return list
    }

    //Удаление фотографии
    fun onPhotoDeleteClicked(photo: ProcessingPhoto) {
        launch {
            photoInteractor.deletePhoto(photo)
            val position = list.indexOfFirst { it.item == arrayListOf(photo) }
            if (position != -1) {
                if (list[position].item.size == 1) {
                    list.removeAt(position)
                } else {
                    list[position].item.removeAt(position)
                }

                viewState.showPhoto(list)
            }
        }
    }

    private suspend fun fetchTroubleReasons() {
        val result = routeInteractor.getContainerTroubleReasons()
        handleResult(result, {
            localFailureReasonsCache = it.data
        }, { handleError(it, rm) })
    }

    private fun fetchContainersLevelsList() {
        val levelsList = commonDataRepository.getContainerLevelsList()
        handleResult(levelsList, { localLoadLevelsCache = it.data }, { handleError(it, rm) })
    }

    private fun fetchStartedRoute() {
        launch {
            val route = routeInteractor.getStartedRouteInfo(
                syncAval = ConnectivityUtils.syncAvailability(
                    routeInteractor.getContext(),
                    ConnectivityUtils.DataType.SECONDARY
                )
            )
            handleResult(route, { localRouteCache = it.data }, { handleError(it, rm) })
        }
    }

    private fun fetchCurrentTask() {
        val res = routeInteractor.getCurrentTask()
        handleResult(res, {
            GlobalScope.launch {
                val taskDraftData = routeInteractor.getDraftByTaskID(it.data.id.toLong())
                handleResult(taskDraftData, { tdRes ->
                    localTaskDraftProcessingResult = tdRes.data
                    setDraftDataToClean(
                        localTaskDraftProcessingResult
                    )
                }, {})

            }
            localTaskCache = it.data
        }, {
            handleError(it, rm)
        })
    }

    fun getContainerName(taskItem: TaskItem): String? =
        localTaskCache.stand?.containerGroups?.map { cg -> cg.containerType }
            ?.find { taskItem.containerTypeId == it.id }?.name

    private fun updateUI() {
        try {
            viewState.setTaskInfo(localTaskCache.stand!!.address)
            viewState.setContainerAction(localTaskCache.containerAction.caption)

            if (localTaskCache.containerAction.name == "PICKUP"
                || localTaskCache.containerAction.name == "REPLACE"
            ) {
                pickupTask = localTaskCache.taskItems.firstOrNull()
            }
            viewState.setState(
                GarbageLoadScreenState(
                    localLoadLevelsCache,
                    getListOfDefaultContainers(),
                    localFailureReasonsCache
                )
            )
            getListOfDefaultContainers().forEach {
                if (it.containerStatus != null){
                    viewState.setActionCompleted(false)
                }
            }
            for (i in getListOfDefaultContainers()){
                if (i.containerStatus != null){
                    viewState.setActionCompletedBottom(true)
                    break
                }else{
                    viewState.setActionCompletedBottom(false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val id = localTaskCache
        if (containerStatuses.isNotEmpty()){
            routeInteractor.isEdited = true
            routeInteractor.taskId = id.id
        }
    }

    private fun getListOfDefaultContainers(): List<StatusTaskExtended> {
        val ste: MutableList<StatusTaskExtended> = mutableListOf()
        for (taskItem in localTaskCache.taskItems) {
            ste.addAll(taskItem.statuses.map {
                StatusTaskExtended(
                    it.id,
                    localTaskCache.stand!!.containerGroups.find { containerGroup ->
                        containerGroup.containerType.id == it.containerTypeId
                    }!!.containerType,
                    it.taskItemId,
                    containerStatuses.find { containerStatus -> containerStatus.id == it.id.toLong() },
                    rule = taskItem.rule,
                    containerAction = localTaskCache.containerAction.caption,
                    containerGroups =  localTaskCache.stand!!.containerGroups.find { containerGroup ->
                        containerGroup.containerType.id == it.containerTypeId
                    }!!.garbageType
                )
            })
        }
        return ste
    }

    private fun getListOfCountableContainers(): List<TaskItemExtended> {
        val taskItems = if (pickupTask == null) {
            localTaskCache.taskItems
        } else
            localTaskCache.taskItems.minus(pickupTask!!)
        return taskItems.map { ti ->
            val cg =
                localTaskCache.stand?.containerGroups
                    ?.find { cg -> cg.containerType.id == ti.containerTypeId }?.containerType!!
            TaskItemExtended(
                ti.id, ti.routeTaskId, cg, ti.planCount, ti.planVolume,
                //Хоба, получай, производительность тупая
                //fixme На самом деле нужно будет как-нибудь поправить в апишке, чтобы не подтягивать все вот так
                ti.statuses.map { st ->
                    StatusTaskExtended(st.id, cg, st.taskItemId,
                        containerStatuses.find { containerStatus -> containerStatus.id == st.id.toLong() }
                    )
                }, ti.detourPoints, ti.paymentOverflow, ti.customer, ti.contract
            )
        }
    }

    private fun removeOldContainerStatus(id: Long) {
        val index = containerStatuses.indexOfFirst { it.id == id }
        if (index != -1) {
            containerStatuses.removeAt(index)
        }
    }

    fun onContainerCountEntered(result: CountableContainersResult) {
        if (result.numberOfDoneContainers > result.taskItem.planCount) {
            for (item in result.taskItem.statuses) {
                removeOldContainerStatus(item.id.toLong())
                return
            }
        }
        if (result.numberOfOverweightContainers > result.numberOfDoneContainers) {
            for (item in result.taskItem.statuses) {
                removeOldContainerStatus(item.id.toLong())
                return
            }
        }
        val doneList = result.taskItem.statuses.take(result.numberOfDoneContainers)
        val unDoneList =
            result.taskItem.statuses.takeLast(result.taskItem.statuses.size - result.numberOfDoneContainers)
        val resList = mutableListOf<ContainerStatus>()
        for (item in doneList) {
            resList.add(
                ContainerStatus(
                    item.id.toLong(), null, item.containerType.id,
                    result.taskItem.contract.id, Date().time,
                    ContainerStatusType("", ContainerStatusType.Type.SUCCESS),
                    0.9, 1.0
                )
            )
        }

        val overweightList = resList.take(result.numberOfOverweightContainers)
        for (item in overweightList) {
            item.volumePercent = 1.25
        }

        for (item in unDoneList) {
            resList.add(
                ContainerStatus(
                    item.id.toLong(), localFailureReasonsCache[0],
                    item.containerType.id,
                    localTaskCache.taskItems.first().contract.id,
                    Date().time,
                    ContainerStatusType("", ContainerStatusType.Type.FAILED),
                    0.0,
                    0.0
                )
            )
        }

        for (res in resList) {
            // TODO: проверить что за хуйня раньше res не мог быть null
            res.id?.let { removeOldContainerStatus(it) }
        }
        containerStatuses.addAll(resList)
        saveTaskDataDraft()

    }

    fun elementLoadLevelChosen(
        statusTaskExtended: StatusTaskExtended,
        containerLoadLevel: ContainerLoadLevel,
        save: Boolean = true
    ) {
        val status = ContainerStatus(
            statusTaskExtended.id.toLong(),
            null,
            statusTaskExtended.containerType.id,
            localTaskCache.taskItems.first().contract.id,
            Date().time,
            ContainerStatusType("", ContainerStatusType.Type.SUCCESS),
            0.9,
            containerLoadLevel.value
        )
        removeOldContainerStatus(statusTaskExtended.id.toLong())
        containerStatuses.add(status)

        if (save) saveTaskDataDraft()
        updateUI()
    }

    private fun setDraftDataToClean(draftsData: TaskDraftProcessingResult?) {
        val draftContainerStatuses = draftsData?.standResults?.first()?.containerStatuses
        if (!draftContainerStatuses.isNullOrEmpty() && containerStatuses.isNullOrEmpty()) {
            containerStatuses = draftContainerStatuses.toMutableList()
            updateUI()
        }
    }

    fun photoBeforeButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_BEFORE
        openCamera(location)
    }

    fun photoAfterButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_AFTER
        openCamera(location)
    }

    fun onAddProblemButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_TROUBLE
        openCamera(location)
    }

    fun onAddBlockageButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_TROUBLE_BLOCKAGE
        openCamera(location)
    }

    fun photoProblemButtonClicked() {
        viewState.takeProblemPhoto(
            localRouteCache.id.toString(),
            localTaskCache.id.toString(),
        )
    }

    fun getStatusPhoto(boolean: Boolean) {
        photoInteractor.getStatusPhoto(boolean)
    }

    fun onSettingsClicked() {
        viewState.showSettingsMenu(true)
    }

    fun taskDoneButtonClicked(volume: Double, idTask: Int) {
        setPickupValue(volume, idTask)
    }

    fun onQrCodeScannerButtonClicked() {
        router.navigateTo(Screens.QrCodeScanner)
    }

    fun routeButtonClicked() {
        router.navigateTo(Screens.RouteStands)
    }

    private fun setPickupValue(volume: Double, idTask: Int) {
        val pt = pickupTask ?: return
        val containerIds = arrayListOf<Int>()
        localTaskCache.taskItems.map { it.statuses.map { s -> containerIds.add(s.id) } }
        if (containerIds.isNotEmpty()){
            containerIds.map{ cT ->
                valueInt = if (idTask == 0){ cT }else{ idTask }
                val status = ContainerStatus(
                    valueInt.toLong(),
                    null,
                    localTaskCache.stand?.containerGroups
                        ?.find { cg -> cg.containerType.id == pt.containerTypeId }!!.containerType.id,
                    pt.contract.id,
                    Date().time,
                    ContainerStatusType("", ContainerStatusType.Type.SUCCESS),
                    volume,
                    1.0
                )
                removeOldContainerStatus(valueInt.toLong())
                containerStatuses.add(status)
            }
        }
        saveTaskDataDraft()
        updateUI()
    }

    private fun checkCurrentTaskDone(): Boolean =
        ::localTaskCache.isInitialized && routeInteractor.getTaskResultById(localTaskCache.id.toLong()) != null


    fun taskDoneButtonClicked(): Boolean {

        if (checkCurrentTaskDone()) {
            router.exit()
            viewState.setLoadingState(false)
            return true
        }

        // true if at least one container is failed
        val isPartially = containerStatuses.any { it.containerFailureReason != null }

        // container processing type
        val statusType = ProcessingStatusType(
            "",
            if (isPartially) StatusType.PARTIALLY else StatusType.SUCCESS
        )

        // list for stand results (list of container statuses)
        val standResults: MutableList<StandResult> = mutableListOf()

        // photo count of current tasks
        val photos =
            photoInteractor.allPhotosStateFlow.value.filter { it.routeId == localRouteCache.id && it.taskId == localTaskCache.id.toLong() }
        val photosCount = photos.size
        val before = photos.filter { it.photoType == PhotoType.LOAD_BEFORE }.size
        val after = photos.filter { it.photoType == PhotoType.LOAD_AFTER }.size

        // if photo count is zero, послать нахуй водителя
        val isAfterNeeded = routeInteractor.getCurrentRoute().requirePhotoAfter
        val isBeforeNeeded = routeInteractor.getCurrentRoute().requirePhotoBefore
        val isTroublePhotoNeeded = routeInteractor.getCurrentRoute().requireFailurePhoto

        val problems =
            containerStatuses.any { it.statusType?.name  == ContainerStatusType.Type.FAILED }

        val allProblems =
            containerStatuses.all { it.statusType?.name == ContainerStatusType.Type.FAILED }

        if (problems && isTroublePhotoNeeded) {
            val pCount =
                photos.filter { it.photoType == PhotoType.LOAD_TROUBLE || it.photoType == PhotoType.LOAD_TROUBLE_BLOCKAGE }.size
            if (pCount == 0) {
//                viewState.showMessage(rm.getString(R.string.garbage_load_fragment_trouble_photos_needed_warning))
                viewState.textMessageError(rm.getString(R.string.garbage_load_fragment_trouble_photos_needed_warning))
                viewState.setLoadingState(false)
                return true
            }
        }

        if (containerStatuses.size < localTaskCache.taskItems.sumOf { item -> item.statuses.size }) {
//            viewState.showMessage(rm.getString(R.string.garbage_load_fragment_fill_container_info_warning))
            viewState.textMessageError(rm.getString(R.string.garbage_load_fragment_fill_container_info_warning))
            viewState.setLoadingState(false)
            return true
        }

        if ((((before == 0) and isBeforeNeeded) or ((after == 0) and isAfterNeeded)) and !allProblems) {
            when {
                isBeforeNeeded && isAfterNeeded -> {
//                    viewState.showMessage(rm.getString(R.string.error_photo_required))
                    viewState.textMessageError(rm.getString(R.string.error_photo_required))
                    viewState.setLoadingState(false)
                    return true
                }
                isBeforeNeeded -> {
                    viewState.textMessageError(rm.getString(R.string.error_photo_required_before))
//                    viewState.showMessage(rm.getString(R.string.error_photo_required_before))
                    viewState.setLoadingState(false)
                    return true
                }
                isAfterNeeded -> {
//                    viewState.showMessage(rm.getString(R.string.error_photo_required_after))
                    viewState.textMessageError(rm.getString(R.string.error_photo_required_after))
                    viewState.setLoadingState(false)
                    return true
                }
            }
        }

        val allTasks = routeInteractor.getAllTaskInDevise()
        val currentTaskPhotosNameList = photos.map { File(it.photoPath).name }

        val allDeliveredTasksPhotosNameList = arrayListOf<String>()


        allTasks?.forEach { task ->
            val allTaskData = routeInteractor.getDeliveredTask(taskId = task.id.toLong())
            if (allTaskData?.hasPhotos == true) {
                allTaskData.standResults?.forEach { standResult ->
                    standResult.photos?.map { allDeliveredTasksPhotosNameList += it.filename }
                }
            }
        }

        for (photoName in allDeliveredTasksPhotosNameList)
            if (photoName in currentTaskPhotosNameList) {
                Thread.sleep(500L)
                viewState.showMessage(rm.getString(R.string.error_duplicate_data))
                viewState.setLoadingState(false)
                return true
            }

        // result object
        val standResult = StandResult(
            localTaskCache.id.toLong(), routeInteractor.processingArrivalTime?: Date().time, Date().time, Date().time, containerStatuses,
            photosCount ?: 0,
            photos = photos.map { PhotoProcessingForApi.fromProcessingPhoto(it) },
            null
        )
        standResults.add(standResult)
        viewState.setLoadingState(false)
        launch {
//            routeInteractor.addTaskToProcessing(localTaskCache, statusType, standResults, null)
            viewState.setCompleteRoute(localTaskCache, statusType, standResults)
//            router.exit()
        }

        return false
    }

    fun saveTaskDataDraft() {
        val isPartially = containerStatuses.any { it.containerFailureReason != null }
        val statusType = ProcessingStatusType(
            "",
            if (isPartially) StatusType.PARTIALLY else StatusType.SUCCESS
        )
        val standResults: MutableList<StandResult> = mutableListOf()
        val standResult = StandResult(
            localTaskCache.id.toLong(), routeInteractor.processingArrivalTime?: Date().time, Date().time, Date().time,
            containerStatuses, 0, photos = null, null
        )

            standResults.add(standResult)

        try {
            GlobalScope.launch {
                routeInteractor.addDraftTaskToProcessing(localTaskCache, statusType, standResults, null)
            }
        }catch (e: java.lang.Exception){}

    }

    fun onTroubleReasonChosen(taskStatus: List<StatusTaskExtended>, reason: ContainerFailureReason) {
        taskStatus.forEach {
            val status = ContainerStatus(
                it.id.toLong(), reason,
                it.containerType.id,
                localTaskCache.taskItems.first().contract.id,
                Date().time,
                ContainerStatusType("", ContainerStatusType.Type.FAILED),
                0.0,
                0.0
            )
            removeOldContainerStatus(it.id.toLong())
            containerStatuses.add(status)
        }
        saveTaskDataDraft()
        updateUI()
    }

    fun getRoutes(){
        router.navigateTo(Screens.RouteStands)
    }

    fun getPhoto(photo: ProcessingPhoto){
        router.navigateTo(Screens.PhotoViewing(photo))
    }

    //тут открывается камера
    private fun openCamera(location: Location? = null) {
        photoLocation = location
        photoFile = File.createTempFile("temp", ".jpg", dataStorageManager.getCacheDirectory())
        viewState.startExternalCameraForResult(photoFile?.absolutePath ?: return)
    }
}
