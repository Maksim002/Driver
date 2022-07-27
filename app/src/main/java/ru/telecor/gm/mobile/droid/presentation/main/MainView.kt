package ru.telecor.gm.mobile.droid.presentation.main

import moxy.viewstate.strategy.alias.SingleState
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.main
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@SingleState
interface MainView : BaseView {

    fun showBatteryLevel(string: String)

    fun showCurrentTime(string: String)

    fun showLocationState(value: Boolean)

    fun showInternetConnection(connectionAvailable: Boolean)

    fun setDataExportingState(isExporting: Boolean)

    fun setConnectionEstablishedState(isConnected: Boolean)

    fun showSettingsMenu()

    fun showConfirmExitDialog()

    fun setContainersCount(count: Int)
}
