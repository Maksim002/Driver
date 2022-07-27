package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_photo_garbage_container.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel

class AdapterPhotoContainer(
    var it: ArrayList<GarbagePhotoModel> = arrayListOf(),
    private val deleteOption: (ProcessingPhoto) -> Unit,
    private val photo: (ProcessingPhoto) -> Unit
) : GenericRecyclerAdapter<GarbagePhotoModel>(it) {

    val list: ArrayList<ProcessingPhoto> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_photo_garbage_container)
    }

    override fun bind(item: GarbagePhotoModel, holder: ViewHolder) = with(holder.itemView) {
        titleType.setTextColor(resources.getColor(R.color.color_text_w))
        val adapters = AdapterPhoto( deleteOption = deleteOption, photo = photo)
        when(item.type){
            "LOAD_BEFORE" ->{
                titleType.text = "ДО ПОГРУЗКИ"
                titleType.isVisible = true
                adapters.update(item.item)
            }
            "LOAD_AFTER" ->{
                titleType.text = "ПОСЛЕ ПОГРУЗКИ"
                titleType.isVisible = true
                adapters.update(item.item)
            }
            "LOAD_TROUBLE" ->{
                titleType.setTextColor(resources.getColor(R.color.color_red_text))
                titleType.text = "НЕВЫВОЗ"
                titleType.isVisible = true
                adapters.update(item.item)
            }
            "LOAD_TROUBLE_BLOCKAGE" ->{
                titleType.text = "НАВАЛ МУСОРА НА ПЛОЩАДКЕ"
                titleType.isVisible = true
                adapters.update(item.item)
            }
            else ->{
                titleType.isVisible = false
            }
        }
        recyclerView.adapter = adapters
    }
}