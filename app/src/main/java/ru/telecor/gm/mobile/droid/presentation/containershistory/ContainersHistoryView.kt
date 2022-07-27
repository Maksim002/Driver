package ru.telecor.gm.mobile.droid.presentation.containershistory

import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.containershistory
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 23.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
interface ContainersHistoryView : BaseView {

    fun setHistoryList(list: List<Pair<CarryContainerHistoryItem, Boolean>>)

}