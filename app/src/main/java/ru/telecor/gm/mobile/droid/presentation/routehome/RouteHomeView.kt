package ru.telecor.gm.mobile.droid.presentation.routehome

import android.graphics.Bitmap
import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.routehome
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 04.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface RouteHomeView : BaseView {

    fun setImProfile( boolean: Boolean, stream: Bitmap? = null)

    fun setDriverInfo(driverInfo: DriverInfo)

    fun setRouteInfo(routeInfo: RouteInfo)

    fun setFirstLoaderList(list: List<LoaderInfo>)

    fun setSecondLoaderList(list: List<LoaderInfo>)

    /**
     * Sets the style of second dropdown. This is needed because second loader dropdown must
     * be disabled until the first loader was selected. When the first loader becomes unselected,
     * this dropdown must become disabled again
     *
     * The view is disabled by default
     */

    fun showNeedPermissionMessage()

    fun setLoadingState(value: Boolean)
}
