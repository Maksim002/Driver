package ru.telecor.gm.mobile.droid.ui.containershistory.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_history_container.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.entities.ContainerHistoryActionType
import ru.telecor.gm.mobile.droid.extensions.color
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.containershistory.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 23.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
class ContainersHistoryViewHolder(override val containerView: View) :
    BaseViewHolder<Pair<CarryContainerHistoryItem, Boolean>>(containerView) {

    override fun bind(entity: Pair<CarryContainerHistoryItem, Boolean>) {
        tvContainerId.text = entity.first.carryContainer.id
        when (entity.first.containerActionType) {
            ContainerHistoryActionType.ADD -> {
                tvActionType.text = "Загружен"
                tvActionType.setTextColor(this.containerView.context.color(R.color.colorAccent))
            }
            ContainerHistoryActionType.REMOVE -> {
                tvActionType.text = "Выгружен"
                tvActionType.setTextColor(this.containerView.context.color(R.color.colorPrimary))
            }
        }
        tvTime.text = SimpleDateFormat("HH:mm", Locale.ROOT).format(entity.first.time)
        tvOnBoardStatus.visible(entity.second)
    }

    companion object {

        fun create(parent: ViewGroup) = ContainersHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history_container, parent, false)
        )
    }
}