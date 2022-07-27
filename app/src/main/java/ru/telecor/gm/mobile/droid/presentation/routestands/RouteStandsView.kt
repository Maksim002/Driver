package ru.telecor.gm.mobile.droid.presentation.routestands

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.routestands
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface RouteStandsView : BaseView {

    fun setLoadingState(value: Boolean)

    fun setTasksList(list: List<TaskRelations>, item: ArrayList<GetListCouponsModel>? = null)

    @Skip
    fun scrollRvTo(position: Int)

    @Skip
    fun showDumpingConfirmationDialog()

    @Skip
    fun showPolygonSelectionDialog(list: List<VisitPoint>)

    @Skip
    fun showPolygonEnsureDialog(visitPoint: VisitPoint)

    @Skip
    fun goBackToTask()

    fun setEmptyListState(value: Boolean)

    fun showInternetConnection(value: Boolean)

    fun showSettingsMenu(boolean: Boolean)

    fun showCurrentTime(string: String)

    fun showBatteryLevel(string: String)

    @Skip
    fun enableButton(enable: Boolean)

    @Skip
    fun showLoading(show:Boolean)
}
