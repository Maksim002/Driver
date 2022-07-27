package ru.telecor.gm.mobile.droid.ui.routestart.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_route.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.routestart.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 03.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class RouteInfoViewHolder(
    override val containerView: View,
    private val clickLambda: (RouteInfo) -> Unit
) : BaseViewHolder<RouteInfo>(containerView) {

    override fun bind(entity: RouteInfo) {
        tvRegNumber.text = entity.unit.vehicle.regNumber
        tvModelName.text = entity.unit.vehicle.model
        val strDate = SimpleDateFormat("dd.MM.yyyy").format(Date(entity.unit.plannedBeginTime))
        val strBeginTime = SimpleDateFormat("HH:mm").format(Date(entity.unit.plannedBeginTime))
        val strEndTime = SimpleDateFormat("HH:mm").format(Date(entity.unit.plannedEndTime))
        tvTime.text = "$strDate $strBeginTime-$strEndTime"

        btnContinue.setOnClickListener {
            clickLambda(entity) }
    }

    companion object {
        fun create(parent: ViewGroup, clickLambda: (RouteInfo) -> Unit) = RouteInfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false),
            clickLambda
        )
    }
}
