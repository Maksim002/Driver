package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task_completed.view.*
import kotlinx.android.synthetic.main.item_task_completed.view.containerTxt
import kotlinx.android.synthetic.main.item_task_completed.view.garbageStatus
import kotlinx.android.synthetic.main.item_task_completed.view.garbageTxt
import kotlinx.android.synthetic.main.item_task_completed.view.layout
import kotlinx.android.synthetic.main.item_task_completed.view.unloadingElementsTxt
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder

class TaskCompletedAdapter(item: ArrayList<TaskItemPreviewData> = arrayListOf()) :
    GenericRecyclerAdapter<TaskItemPreviewData>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_task_completed)
    }

    override fun bind(item: TaskItemPreviewData, holder: ViewHolder): Unit = with(holder.itemView) {
        if (item.containerAction != null) {
            garbageStatus.text = item.containerAction.caption
        }
        if (item.statusType != null) {
            when (item.statusType) {
                "Успешно" -> {
                    layout.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.grey_color)
                    mark.background =
                        resources.getDrawable(R.drawable.circle_grin_mark_background_layout, null)
                    containerImage.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.grey_color
                        )
                    )
                }
                "Частично успешно" -> {
                    layout.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.yellow_color)
                    mark.background =
                        resources.getDrawable(R.drawable.circle_yellow_mark_background_layout, null)
                    containerImage.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.yellow_color
                        )
                    )
                }
                "Невывоз" -> {
                    layout.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.black_to_red_color)
                    mark.background =
                        resources.getDrawable(R.drawable.circle_red_mark_background_layout, null)
                    containerImage.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.black_to_red_color
                        )
                    )
                }
            }
        }

        val doneCount =
            item.taskResultData?.standResults?.firstOrNull()?.containerStatuses?.filter { rt -> rt.containerTypeId == item.containerType.id && rt.statusType?.name.toString() != "FAILED" }?.size
                ?: item.taskDraftData?.standResults?.firstOrNull()?.containerStatuses?.filter { rt -> rt.containerTypeId == item.containerType.id && rt.statusType?.name.toString() != "FAILED" }?.size

        if (doneCount != null) {
            if (doneCount != 0) {
                unloadingElementsTxt.text = "${doneCount ?: 0} / ${item.count}"
            }else{
                unloadingElementsTxt.text = "${item.count}"
            }
        } else {
            layout.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.gray2)
            unloadingElementsTxt.text = "${item.count}"
        }


        containerTxt.text = item.containerType.name
        garbageTxt.text = item.garbageType.name
    }
}