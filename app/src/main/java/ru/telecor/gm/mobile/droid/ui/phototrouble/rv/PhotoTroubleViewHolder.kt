package ru.telecor.gm.mobile.droid.ui.phototrouble.rv;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_photo.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

class PhotoTroubleViewHolder(
    override val containerView: View,
    private val deleteOption: (ProcessingPhoto) -> Unit
) : BaseViewHolder<ProcessingPhoto>(containerView) {

    override fun bind(entity: ProcessingPhoto) {
        Glide.with(containerView.context)
            .load(entity.photoPath)
            .into(ivPhoto)

        itemView.setOnLongClickListener {
            val popup = PopupMenu(itemView.context, itemView)
            popup.menuInflater.inflate(R.menu.photo_actions, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_action -> {
                        deleteOption(entity)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }
    }

    companion object {
        fun create(parent: ViewGroup, deleteOption: (ProcessingPhoto) -> Unit) =
            PhotoTroubleViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_photo_trouble, parent, false),
                deleteOption
            )
    }
}
