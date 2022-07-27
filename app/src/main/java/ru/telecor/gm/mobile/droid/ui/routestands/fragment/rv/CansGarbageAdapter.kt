package ru.telecor.gm.mobile.droid.ui.routestands.fragment.rv

import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_garbage_cans_new.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder

class CansGarbageAdapter(
    item: ArrayList<TaskItemPreviewData> = arrayListOf(),
    private val clickLambda: (TaskRelations) -> Unit
) : GenericRecyclerAdapter<TaskItemPreviewData>(item) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_garbage_cans_new)
    }

    override fun bind(item: TaskItemPreviewData, holder: ViewHolder) = with(holder.itemView) {
        containerTxt.text = item.containerType.name
        garbageTxt.text = item.containerAction?.caption
        unloadingElementsTxt.text = item.count.toString()

        this.setOnClickListener { clickLambda(item.taskRelations!!) }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (item.taskRelations?.task?.statusType == StatusType.NEW) {
                garbageView.backgroundTintList =
                    ContextCompat.getColorStateList(context,R.color.color_text_w)
            }

            if (item.taskRelations?.task?.statusType == StatusType.FAIL) {
                garbageView.backgroundTintList =
                    ContextCompat.getColorStateList(context,R.color.error_mark)
            }

            if (item.taskRelations?.task?.statusType == StatusType.SUCCESS) {
                garbageView.backgroundTintList =
                    ContextCompat.getColorStateList(context,R.color.grey_color)
            }

            if (item.taskRelations?.task?.statusType == StatusType.PARTIALLY) {

                //TODO преверка на пустоту контейнеров или невывоза
                garbageView.backgroundTintList =
                    ContextCompat.getColorStateList(context,R.color.error_mark)
            }

//        }
//        garbageStatus.text = item.containerAction?.caption.emptyIfNull()

//        if (item.garbageType.name == "ЖБО") {
//            layout.isVisible = false
//            hidingWindow(holder)
//        }

//        if (item.taskRelations!!.taskProcessingResult != null) {
//            if (item.taskRelations!!.task.id == item.taskRelations!!.taskProcessingResult!!.standResults!!.first {
//                    it.id!!.toInt() == item.taskRelations!!.task.id
//                }.id!!.toInt()) {
//                item.taskRelations!!.taskProcessingResult?.standResults?.map {
//                    var zeroNum = 0
//                    val equatedNum = it.containerStatuses.size
//                    for (i in 1..it.containerStatuses.size) {
//                        if (it.containerStatuses[i - 1].statusType?.name.toString() == "SUCCESS") zeroNum++
//                        if (i == it.containerStatuses.size) {
//                            unloadingElementsTxt.text = "$zeroNum / $equatedNum"
//                        }
//                    }
//                }
//            }
//        } else {
////            layout.isVisible = false
//            hidingWindow(holder)
//        }
    }

    private fun hidingWindow(holder: ViewHolder) = with(holder.itemView) {
//        commentImage.isClickable = false
        containerTypeCount.background =
            resources.getDrawable(R.drawable.circle_background_text, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerTypeCount.backgroundTintList =
                holder.itemView.context.getColorStateList(R.color.gray2)
        }
    }
}