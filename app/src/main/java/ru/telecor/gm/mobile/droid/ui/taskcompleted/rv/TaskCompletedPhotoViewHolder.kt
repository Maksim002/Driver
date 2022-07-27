package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.installations.Utils
import kotlinx.android.synthetic.main.item_completed_photo.*
import kotlinx.android.synthetic.main.item_photo_garbage.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder
import ru.telecor.gm.mobile.droid.utils.getDateTime
import ru.telecor.gm.mobile.droid.utils.timeDate

class TaskCompletedPhotoViewHolder(
    override val containerView: View,
    private val photo: (ProcessingPhoto) -> Unit
) :
    BaseViewHolder<ProcessingPhoto>(containerView) {

    override fun bind(entity: ProcessingPhoto) {
        textViewData.text =  timeDate(entity.timestamp.toLong())
        Glide.with(containerView.context)
            .load(entity.photoPath)
            .centerCrop()
            .into(itemProblemPhoto)

        itemProblemPhoto.setOnClickListener {
            photo.invoke(entity)
        }
    }

    companion object {
        fun create(parent: ViewGroup, photo: (ProcessingPhoto) -> Unit) =
            TaskCompletedPhotoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_completed_photo, parent, false), photo
            )
    }
}
