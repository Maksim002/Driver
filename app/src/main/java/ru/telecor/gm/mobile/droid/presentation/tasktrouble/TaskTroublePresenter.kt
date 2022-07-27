package ru.telecor.gm.mobile.droid.presentation.tasktrouble

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.db.PhotoProcessingForApi
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.ui.tasktrouble.fragment.ProblemBottomSheetFragment
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

@InjectViewState
class TaskTroublePresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val photoInteractor: PhotoInteractor,
    private val rm: ResourceManager,
    private val router: Router
) : BasePresenter<TaskTroubleView>() {

    var photoPath: String = ""

    lateinit var localTaskCache: TaskExtended
    lateinit var localRouteCache: RouteInfo
    var trouble: TaskFailureReason? = null
    private var reasons: List<TaskFailureReason> = listOf()
    val list: ArrayList<GarbagePhotoModel> = arrayListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadTroubleReasons()
        launch {
            val result = routeInteractor.getCurrentTask()
            handleResult(result, {
                localTaskCache = it.data
            }, { handleError(it, rm) })

            val route = routeInteractor.getStartedRouteInfo()
            handleResult(route, {
                localRouteCache = it.data

                //Заполнение адаптера фтоографиями
                for (modelType in 1..PhotoType.values().size) {
                    launch {
                        val type = PhotoType.values()[modelType - 1]
                        photoInteractor.getTaskPhotosFlow(
                            localRouteCache.id, localTaskCache.id.toLong(), type
                        ).collect { it ->
                            if (it.isNotEmpty()) {
                                if (type == PhotoType.TASK_TROUBLE)
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
            }, {
                handleError(it, rm)
            })
            if (::localTaskCache.isInitialized) {
                viewState.setTroubleAdditionalInfo(
                    Date().time,
                    localTaskCache.stand?.latitude ?: 0.0,
                    localTaskCache.stand?.longitude ?: 0.0
                )
                updateUI()
            }
        }
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
                list[position].item.removeAt(position)
//                if (list[position].item.size == 1) {
//                    list.removeAt(position)
//                } else {
//                    list[position].item.removeAt(position)
//                }
                viewState.showPhoto(list)
            }
        }
    }

    private fun loadTroubleReasons() {
        viewState.setLoadingState(true)
        launch {
            val list = routeInteractor.getTroubleReasons()
            handleResult(list, {
                    reasons = it.data
                    viewState.setTroubleDropdownList(
                        it.data
                    )
                },
                { handleError(it, rm) })
            viewState.setLoadingState(false)
        }

    }

    private fun updateUI() {
        try {
            viewState.setTaskAddress(localTaskCache.stand!!.address)
            viewState.setRecyclerData(getListOfDefaultContainers())
            viewState.setGeneralCan(localTaskCache)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getListOfDefaultContainers(taskStatus: TaskFailureReason? = null): List<StatusTaskExtended> {
        val ste: MutableList<StatusTaskExtended> = mutableListOf()
        for (taskItem in localTaskCache.taskItems) {
            ste.addAll(taskItem.statuses.map {
                StatusTaskExtended(
                    it.id,
                    localTaskCache.stand!!.containerGroups.find { containerGroup ->
                        containerGroup.containerType.id == it.containerTypeId
                    }!!.containerType,
                    it.taskItemId,
                    rule = taskItem.rule,
                    containerAction = localTaskCache.containerAction.caption,
                    containerGroups = localTaskCache.stand!!.containerGroups.find { containerGroup ->
                        containerGroup.containerType.id == it.containerTypeId
                    }!!.garbageType,
                    taskStatus =  taskStatus
                )
            })
        }
        return ste
    }

    fun photoButtonClicked() {
        viewState.takePhoto(localRouteCache.id.toString(), localTaskCache.id.toString())
    }

//    fun rememberPath(path: String) {
//        photoPath = path
//    }
//
//    fun photoDone() {
//        if (photoPath == "") {
//            viewState.showMessage("Error creating photo")
//        } else
//            viewState.setPhotoPreview(photoPath)
//    }


    fun getPhoto(photo: ProcessingPhoto){
        router.navigateTo(Screens.PhotoViewing(photo))
    }

    fun onTroubleReasonSelected(position: Int) {
        trouble = reasons[position]
        viewState.setRecyclerData(getListOfDefaultContainers(trouble!!))
    }

    fun confirmButtonClicked(bottomFragment : ProblemBottomSheetFragment): Boolean {

        if (trouble == null) {
            viewState.showMessage(rm.getString(R.string.task_trouble_fragment_no_reason_warning))
            return true
        }
        val photos =
            photoInteractor.allPhotosStateFlow.value.filter { it.routeId == localRouteCache.id && it.taskId == localTaskCache.id.toLong() }
        val photoCount = photos.size

        if (photoCount == 0) {
            val route = routeInteractor.getCurrentRoute()
            if (route.requireFailurePhoto) {
                viewState.showMessage(rm.getString(R.string.task_trouble_photo_required_message))
                return true
            }
        }

        launch {
            val standResult = StandResult(
                localTaskCache.id.toLong(), routeInteractor.processingArrivalTime?: Date().time,
                Date().time, Date().time, listOf(),
                photoCount, photos.map { PhotoProcessingForApi.fromProcessingPhoto(it) }, null, true
            )
            routeInteractor.addTaskToProcessing(
                localTaskCache,
                ProcessingStatusType("", StatusType.FAIL),
                listOf(standResult), trouble
            )
            router.exit()
        }
        bottomFragment.dismiss()
        return false
    }

    fun onBackClicked() {
        router.exit()
    }
}
