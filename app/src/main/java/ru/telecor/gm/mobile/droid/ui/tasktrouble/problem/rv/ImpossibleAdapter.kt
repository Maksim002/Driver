package ru.telecor.gm.mobile.droid.ui.tasktrouble.problem.rv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_impossible_container.view.*
import kotlinx.android.synthetic.main.item_impossible_container.view.containerTxt
import kotlinx.android.synthetic.main.item_impossible_container.view.garbageStatus
import kotlinx.android.synthetic.main.item_impossible_container.view.garbageTxt
import kotlinx.android.synthetic.main.item_impossible_container.view.mark
import kotlinx.android.synthetic.main.item_impossible_container.view.statusDescription
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended

class ImpossibleAdapter(item: ArrayList<StatusTaskExtended> = arrayListOf()): GenericRecyclerAdapter<StatusTaskExtended>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_impossible_container)
    }

    override fun bind(item: StatusTaskExtended, holder: ViewHolder): Unit = with(holder.itemView) {
        garbageStatus.text = item.containerAction
        containerTxt.text = item.containerType.name
        garbageTxt.text = item.containerGroups?.name
        if (item.taskStatus != null){
            statusDescription.text = item.taskStatus.name
            statusDescription.isVisible = true
            statusDescription.setTextColor(ContextCompat.getColor(context, R.color.black_to_red_color))
            mark.background = resources.getDrawable(R.drawable.circle_red_mark_background_layout, null)
            containerImage.setColorFilter(ContextCompat.getColor(context, R.color.black_to_red_color))
        }
    }
}