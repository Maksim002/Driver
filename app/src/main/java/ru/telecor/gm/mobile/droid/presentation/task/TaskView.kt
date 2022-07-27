package ru.telecor.gm.mobile.droid.presentation.task

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.task
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface TaskView : BaseView {

    @Skip
    fun startNavigationApplication(lat: Double, lon: Double)

    fun setRouteNumber(str: String)

    fun setLoadLevel(str: String)

    fun setAddress(str: String)

    fun setCustomerInfo(str: String)

    fun setContactNumber(str: String)

    fun setPreferredTime(str: String)

    fun setPlanTimeSten(str: String)

    fun setComment(str: String)

    fun setPriority(str: String)

    fun setRouteComment(str: String)

    fun setTaskList(taskItemPreviewData: List<TaskItemPreviewData>)

    fun taskShowMode(mode: Mode)

    @Skip
    fun showLoadDialog(standName: String)

    fun setLoadingState(value: Boolean)

    fun setLastTaskMode(value: Boolean)

    @Skip
    fun showFinishDialog()

    fun showPolygonRequestLoadingDialog(value: Boolean)

    fun showPolygonRequestRetryDialog()

    fun showInternedNeeded()

    @Skip
    fun showPolygonSelectionDialog(variantsList: List<VisitPoint>)

    @Skip
    fun showPolygonSelectionEnsureDialog(selected: VisitPoint)

    fun showCurrentTime(string: String)

    fun showBatteryLevel(string: String)

    fun setClickable(clickable: Boolean = true)

    fun showSettingsMenu()

    fun showMapLocation(
        lat: Double,
        lon: Double,
        standGeoJson: String? = null,
        evacuationGeoJson: String? = null,
        routeToRoad: String? = null
    )

    @Skip
    fun openFullScreenMap(
        lat: Double,
        lon: Double,
        standGeoJson: String? = null,
        evacuationGeoJson: String? = null,
        routeToRoad: String? = null
    )

    enum class Mode {
        CURRENT,
        DONE,
        PREVIEW
    }
}
