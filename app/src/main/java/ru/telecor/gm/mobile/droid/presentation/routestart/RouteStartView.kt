package ru.telecor.gm.mobile.droid.presentation.routestart

import android.graphics.Bitmap
import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BaseView
import ru.terrakok.cicerone.Router
import java.io.InputStream

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.routestart
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface RouteStartView : BaseView {

    fun setLoadingState(value: Boolean)

    fun setAlert(value: Boolean)

    fun setRoutesList(list: List<RouteInfo>)

    fun setAlertDialog(router: Router)

    fun setDriver(driver: DriverInfo)

    fun sendLoginRequestAndUpdate()

    fun setFirstLoaderPreselected(loaderInfo: LoaderInfo)
    fun setSecondLoaderPreselected(loaderInfo: LoaderInfo)

    @Skip
    fun showUpdateDialog(version: BuildVersion)

    fun setImProfile(boolean: Boolean, stream: Bitmap? = null)

    @Skip
    fun setFirstLoaderList(list: List<LoaderInfo>)

    @Skip
    fun setSecondLoaderList(list: List<LoaderInfo>)

    @Skip
    fun setRouteInfo(routeInfo: RouteInfo)

    fun showCurrentTime(string: String)

    fun showBatteryLevel(string: String)

    fun showLackInternet(error: String)
}
