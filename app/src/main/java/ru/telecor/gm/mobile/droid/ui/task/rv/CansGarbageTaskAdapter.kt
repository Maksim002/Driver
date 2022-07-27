package ru.telecor.gm.mobile.droid.ui.task.rv

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_garbage_cans.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder

class CansGarbageTaskAdapter(
    item: ArrayList<TaskItemPreviewData> = arrayListOf(),
) : GenericRecyclerAdapter<TaskItemPreviewData>(item) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_garbage_cans)
    }

    override fun bind(item: TaskItemPreviewData, holder: ViewHolder) = with(holder.itemView) {
        item.task!!.forEach {
            if (it.service != null) {
                // TODO: 24.06.2022 Временно скрываем
                titleMessage.isVisible = false
                titleMessage.text = it.service.name
            } else {
                titleMessage.isVisible = false
            }
            if (it.comment != "" && it.comment.toString() != "null") {
                // TODO: 24.06.2022 Временно скрываем
                messageText.isVisible = false
                messageText.text = it.comment
            } else {
                messageText.isVisible = false
            }
        }

        containerTxt.text = item.containerType.name
        garbageTypeTxt.text = item.garbageType.name
        garbageTxt.text = item.garbageType.name
        garbageStatus.text = item.containerAction?.caption.emptyIfNull()

        if (item.garbageType.name == "ЖБО") {
            hidingWindow(holder)
        }
        val doneCount =
            item.taskDraftData?.standResults?.firstOrNull()?.containerStatuses?.filter { rt -> rt.containerTypeId == item.containerType.id }?.size
                ?: 0

        if (item.taskRelations != null) {
            if (item.taskRelations!!.taskProcessingResult != null) {
                if (item.taskRelations!!.task.id == item.taskRelations!!.taskProcessingResult!!.standResults!!.first {
                        it.id!!.toInt() == item.taskRelations!!.task.id
                    }.id!!.toInt()) {
                    item.taskRelations!!.taskProcessingResult?.standResults?.map {
                        var zeroNum = 0
                        val equatedNum = it.containerStatuses.size
                        for (i in 1..it.containerStatuses.size) {
                            if (it.containerStatuses[i - 1].statusType!!.name.toString() == "SUCCESS") zeroNum++
                            if (i == it.containerStatuses.size) {
                                unloadingElementsTxt.text = "$zeroNum / $equatedNum"
                            }
                        }
                    }
                }
            } else {
                hidingWindow(holder)
            }
        } else {
            unloadingElementsTxt.text = "$doneCount / ${item.count}"
            layout.backgroundTintList =  ContextCompat.getColorStateList(context,if (doneCount == item.count)R.color.green_text else R.color.gray2)
        }
    }
}

private fun hidingWindow(holder: ViewHolder) = with(holder.itemView) {
    layout.isClickable = false
    layout.background = resources.getDrawable(R.drawable.circle_background_text_grey, null)
}