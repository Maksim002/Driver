package ru.telecor.gm.mobile.droid.presentation.taskcompleted


import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface TaskCompletedView : BaseView {

    fun setAddress(str: String)

    fun showSettingsMenu()

    fun setContainersList(list: List<TaskItemPreviewData>)

    fun showListOfBeforePhoto(list: List<ProcessingPhoto>)

    fun showListOfAfterPhoto(list: List<ProcessingPhoto>)

    fun showListOfTroublePhoto(list: List<ProcessingPhoto>)

    fun showListOfTroubleTaskPhoto(list: List<ProcessingPhoto>)

    fun setLoadingState(value: Boolean)
}
