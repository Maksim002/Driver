package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_photo_garbage.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.utils.*
import ru.telecor.gm.mobile.droid.utils.getDateTime
import kotlin.collections.ArrayList

class AdapterPhoto(
    var item: ArrayList<ProcessingPhoto> = arrayListOf(),
    private val deleteOption: (ProcessingPhoto) -> Unit,
    private val photo: (ProcessingPhoto) -> Unit
) : GenericRecyclerAdapter<ProcessingPhoto>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_photo_garbage)
    }

    override fun bind(item: ProcessingPhoto, holder: ViewHolder): Unit = with(holder.itemView){
        textViewData.text = timeDate(item.timestamp.toLong())
        itemTroublePhotoRemove.setOnClickListener {
            deleteOption(item)
        }

        this.setOnClickListener {
            photo(item)
        }

        Glide.with(holder.itemView.context)
            .load(item.photoPath)
            .into(itemProblemPhoto)
    }
}