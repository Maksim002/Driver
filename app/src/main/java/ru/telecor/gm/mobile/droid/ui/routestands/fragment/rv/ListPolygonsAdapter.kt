package ru.telecor.gm.mobile.droid.ui.routestands.fragment.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_list_polygons.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.VisitPoint

class ListPolygonsAdapter(var listener: Listener, var list: ArrayList<VisitPoint> = arrayListOf()): GenericRecyclerAdapter<VisitPoint>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_list_polygons)
    }

    override fun bind(item: VisitPoint, holder: ViewHolder) = with(holder.itemView) {
        polygonText.text = item.name
        holder.itemView.setOnClickListener {
            listener.setOnClickPolygons(item)
        }
    }

    interface Listener{
       fun setOnClickPolygons(item: VisitPoint)
    }
}