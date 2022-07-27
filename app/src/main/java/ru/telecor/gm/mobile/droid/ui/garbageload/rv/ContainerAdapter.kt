package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.telecor.gm.mobile.droid.entities.ContainerLoadLevel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter
import ru.telecor.gm.mobile.droid.ui.garbageload.rv.diffutil.VolumeContainerDiffUtilCallback

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.garbageload.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 05.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class ContainerAdapter(
    private var levelList: List<ContainerLoadLevel>,
    private val onLoadLevelClicked: (StatusTaskExtended) -> Unit
) : BaseAdapter<ContainerViewHolder, StatusTaskExtended>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder =
        ContainerViewHolder.create(parent, levelList, onLoadLevelClicked)

    fun setLevelsList(list: List<ContainerLoadLevel>) {
        levelList = list
        notifyDataSetChanged()
    }

    override fun setList(list: List<StatusTaskExtended>) {
        val diffUtilCallback = VolumeContainerDiffUtilCallback(dataSrc, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        dataSrc = list
        diffResult.dispatchUpdatesTo(this)
    }
}
