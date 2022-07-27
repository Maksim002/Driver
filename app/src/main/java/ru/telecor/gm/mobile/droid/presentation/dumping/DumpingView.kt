package ru.telecor.gm.mobile.droid.presentation.dumping

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.CompletedContainerInfo
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.dumping
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 10.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface DumpingView : BaseView {

    fun setAddress(string: String)

    fun setContainersList(list: List<CompletedContainerInfo>)

    @Skip
    fun showWeightDialog()

    @Skip
    fun startNavigationApplication(lat: Double, lon: Double)

    fun showInternetConnection(connectionAvailable: Boolean)

    fun showListCoupons(list: GetListCouponsModel)

    fun setExpectedVolume(value: Double)

    fun setLoadingState(value: Boolean)

    fun showDoingBack()

    fun showRouteClosure(value: Int)

    fun showCurrentTime(string: String)

    fun showBatteryLevel(string: String)
}
