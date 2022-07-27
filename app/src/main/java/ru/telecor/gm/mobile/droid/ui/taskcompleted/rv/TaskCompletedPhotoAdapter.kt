package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

class TaskCompletedPhotoAdapter(private val photo: (ProcessingPhoto) -> Unit) : BaseAdapter<TaskCompletedPhotoViewHolder, ProcessingPhoto>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskCompletedPhotoViewHolder =
        TaskCompletedPhotoViewHolder.create(parent, photo)
}
