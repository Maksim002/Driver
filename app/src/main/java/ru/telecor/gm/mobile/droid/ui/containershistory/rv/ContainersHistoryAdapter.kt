package ru.telecor.gm.mobile.droid.ui.containershistory.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.containershistory.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 23.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
class ContainersHistoryAdapter :
    BaseAdapter<ContainersHistoryViewHolder, Pair<CarryContainerHistoryItem, Boolean>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainersHistoryViewHolder =
        ContainersHistoryViewHolder.create(parent)
}