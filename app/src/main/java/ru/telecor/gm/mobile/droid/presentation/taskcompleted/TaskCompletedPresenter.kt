package ru.telecor.gm.mobile.droid.presentation.taskcompleted

import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class TaskCompletedPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val photoInteractor: PhotoInteractor,
    private val rm: ResourceManager,
    val router: Router
) : BasePresenter<TaskCompletedView>() {

    private lateinit var localTaskCache: TaskExtended
    private var deliveredTask: TaskProcessingResult? = null
    private lateinit var localTaskDraftProcessingResult: TaskDraftProcessingResult
    private var localTaskProcessingResult: TaskProcessingResult? = null

    var taskId: Int = -1

    override fun attachView(view: TaskCompletedView?) {
        super.attachView(view)
        clearUI()
        deliveredTask = routeInteractor.getDeliveredTask(taskId.toLong())

        launch {
            val result = routeInteractor.getTaskById(taskId)
            handleResult(result, {
                GlobalScope.launch {
                    if (it.data.statusType == StatusType.NEW) {
                        val taskDraftData =
                            routeInteractor.getDraftByTaskID(taskId.toLong())
                        handleResult(taskDraftData, { tdRes ->
                            localTaskDraftProcessingResult = tdRes.data
                            updateUI()
                        }, {})
                    } else {
                        localTaskProcessingResult = routeInteractor.getTaskResultById(taskId.toLong())
                        CoroutineScope(Dispatchers.Main).launch {
                            updateUI()
                        }
                    }
                }
                localTaskCache = it.data
                updateUI()
            }, {
                handleError(it, rm)
                clearUI()
            })
        }
    }

    private fun clearUI() {
        viewState.setAddress("")
        viewState.setContainersList(emptyList())
        viewState.showListOfAfterPhoto(emptyList())
        viewState.showListOfBeforePhoto(emptyList())
        viewState.showListOfTroublePhoto(emptyList())
        viewState.showListOfTroubleTaskPhoto(emptyList())
    }

    private fun updateUI() {
        setActualInfo()
    }

    private fun setActualInfo() {
        viewState.setAddress(localTaskCache.stand?.address ?: "")
        val ctList =
            localTaskCache.taskItems.mapNotNull { ti -> localTaskCache.stand?.containerGroups?.findLast {
                    cG -> cG.containerType.id == ti.containerTypeId } }
        var trDraft: TaskDraftProcessingResult? = null
        if (::localTaskDraftProcessingResult.isInitialized) {
            localTaskDraftProcessingResult.let { tD ->
                if (tD.id == localTaskCache.id.toLong()) trDraft = tD
            }
        }
        var tResult: TaskProcessingResult? = null
        localTaskProcessingResult?.let { tD ->
            if (tD.id == localTaskCache.id.toLong()) tResult = tD
        }
        val supportedGarbageTypes =
            routeInteractor.startedRoute?.unit?.vehicle?.supportedGarbageTypes?.map { it.id }
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
                statusType = getStatus(localTaskCache.statusType),
                failureReason = deliveredTask?.failureReason?.name ?: "",
                taskDraftData = trDraft,
                taskResultData = tResult
            )
        }.toMutableList()

        viewState.setContainersList(finalList.sortedByDescending { it.supportedGarbageType.toString() })
        viewState.showListOfAfterPhoto(getPhotoList(PhotoType.LOAD_AFTER))
        viewState.showListOfBeforePhoto(getPhotoList(PhotoType.LOAD_BEFORE))
        viewState.showListOfTroublePhoto(getPhotoList(PhotoType.LOAD_TROUBLE))
        viewState.showListOfTroubleTaskPhoto(getPhotoList(PhotoType.TASK_TROUBLE))
    }

    private fun getPhotoList(photoType: PhotoType): List<ProcessingPhoto> {
        //Это не грех, гуглы сами разрешают так делать
        return runBlocking {
            photoInteractor.getTaskPhotosFlow(
                routeInteractor.startedRoute?.id ?: -1, localTaskCache.id.toLong(), photoType
            ).first()
        }
    }

    fun getPhoto(photo: ProcessingPhoto){
        router.navigateTo(Screens.PhotoViewing(photo))
    }

    fun onSettingsClicked() {
        viewState.showSettingsMenu()
    }

    fun getStatus(statusType: StatusType): String {
        return when (statusType) {
            StatusType.SUCCESS -> rm.getString(R.string.task_completed_fragment_success)
            StatusType.PARTIALLY -> rm.getString(R.string.task_completed_fragment_partially)
            StatusType.FAIL -> rm.getString(R.string.task_completed_fragment_fail)
            else -> ""
        }
    }
}
