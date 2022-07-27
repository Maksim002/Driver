package ru.telecor.gm.mobile.droid.ui.dumping.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.CompletedContainerInfo
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.dumping.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 10.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class CompletedContainersAdapter :
    BaseAdapter<CompletedContainersViewHolder, CompletedContainerInfo>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CompletedContainersViewHolder.create(parent)
}
