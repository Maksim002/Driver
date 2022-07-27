package ru.telecor.gm.mobile.droid.ui.routestands.rv

import android.view.ViewGroup
import com.github.vipulasri.timelineview.TimelineView
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.routestands.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 03.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class RouteStandAdapter(
    private val clickLambda: (TaskRelations) -> Unit
) : BaseAdapter<RouteStandViewHolder, TaskRelations>() {
    private lateinit var routeInteractor: RouteInfo
    private lateinit var holder: RouteStandViewHolder
    lateinit var couponsListCouponsModel: List<GetListCouponsModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteStandViewHolder =
        RouteStandViewHolder.create(parent, viewType, clickLambda, routeInteractor,couponsListCouponsModel)

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    fun routesInfo(info: RouteInfo){
        routeInteractor = info
    }

    fun setCoupons(couponsList:List<GetListCouponsModel>){
        couponsListCouponsModel = couponsList
    }
}
