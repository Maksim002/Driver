package ru.telecor.gm.mobile.droid.ui.phototrouble.rv;

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

class PhotoTroubleAdapter(
    private val deleteAction: (ProcessingPhoto) -> Unit
) : BaseAdapter<PhotoTroubleViewHolder, ProcessingPhoto>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoTroubleViewHolder =
        PhotoTroubleViewHolder.create(parent, deleteAction)
}
