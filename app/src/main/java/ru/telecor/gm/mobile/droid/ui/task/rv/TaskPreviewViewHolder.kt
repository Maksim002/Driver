package ru.telecor.gm.mobile.droid.ui.task.rv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_garbage_task.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.task.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 11.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class TaskPreviewViewHolder(override val containerView: View) :
    BaseViewHolder<TaskItemPreviewData>(containerView) {

    override fun bind(entity: TaskItemPreviewData) {
        tvLoadType.text = entity.containerType.name
        tvNumberOfContainers.text = entity.count.toString()
        tvContainerAction.text = entity.containerAction?.caption.emptyIfNull()
        tvGarbageType.text = entity.garbageType.name
        //Когда с сервера начнут приходить ссылки на иконки, их логику нужно будет писать здесь
        if (!entity.supportedGarbageType) {
            divider.setBackgroundColor(Color.parseColor("#999999"))
            tvLoadType.setTextColor(Color.parseColor("#999999"))
            tvNumberOfContainers.setTextColor(Color.parseColor("#999999"))
            tvContainerAction.setTextColor(Color.parseColor("#999999"))
            tvGarbageType.setTextColor(Color.parseColor("#999999"))
            ivIcon.setColorFilter(Color.parseColor("#999999"))
        }
        Glide.with(containerView.context)
            .load(R.drawable.ic_default_trash_can)
            .into(ivIcon)
    }

    companion object {
        fun create(parent: ViewGroup) =
            TaskPreviewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_garbage_task, parent, false)
            )
    }
}
