package ru.telecor.gm.mobile.droid.presentation.garbageload

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason
import ru.telecor.gm.mobile.droid.entities.ContainerLoadLevel
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.presentation.base.BaseView
import ru.telecor.gm.mobile.droid.presentation.garbageload.util.GarbageLoadScreenState

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.garbageload
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 20.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface GarbageLoadView : BaseView {

    fun setBeforePhotoButtonCompleted()
    fun setAfterPhotoButtonCompleted()
    fun setProblemPhotoButtonCompleted()

    fun setAddButtonVisibility(value: Boolean)

    fun setTaskInfo(name: String)

    fun setActionCompleted(boolean: Boolean? = true)
    fun setActionCompletedBottom(boolean: Boolean? = false)

    fun setContainerAction(action: String)

    @Skip
    fun showContainerTroubleDialog(
        taskStatus: StatusTaskExtended,
        list: List<ContainerFailureReason>
    )

    @Skip
    fun showContainerLevelDialog(
        taskStatus: StatusTaskExtended,
        levelsList: List<ContainerLoadLevel>
    )

    fun showPhoto(model: ArrayList<GarbagePhotoModel>)

    fun setLoadingState(value: Boolean)

    fun textMessageError(textMessage: String)

    fun setCompleteRoute(localTaskCache: TaskExtended, statusType: ProcessingStatusType, standResults: MutableList<StandResult>)

    @Skip
    fun startExternalCameraForResult(path: String)

    @Skip
    fun takePhoto(routeCode: String, taskId: String, type: PhotoType)

    @Skip
    fun takeProblemPhoto(routeCode: String, taskId: String)

    fun setState(state: GarbageLoadScreenState)

    fun showSettingsMenu(boolean: Boolean)

    fun onBeckPressed()
}
