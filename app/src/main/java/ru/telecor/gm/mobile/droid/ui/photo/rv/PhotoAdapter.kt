package ru.telecor.gm.mobile.droid.ui.photo.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

class PhotoAdapter(private val deleteAction: (ProcessingPhoto) -> Unit) :
    BaseAdapter<PhotoViewHolder, ProcessingPhoto>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        PhotoViewHolder.create(parent, deleteAction)
}
