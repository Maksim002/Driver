package ru.telecor.gm.mobile.droid.ui.dumping.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_garbage_dumping.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.CompletedContainerInfo
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.dumping.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 10.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class CompletedContainersViewHolder(override val containerView: View) :
    BaseViewHolder<CompletedContainerInfo>(containerView) {

    override fun bind(containerInfo: CompletedContainerInfo) {
        tvContainersType.text = containerInfo.typeName
        tvContainersAmount.text = containerInfo.count.toString()
        tvContainersVolume.text = containerInfo.volume.toString()
    }

    companion object {
        fun create(parent: ViewGroup) = CompletedContainersViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_garbage_dumping, parent, false)
        )
    }
}
