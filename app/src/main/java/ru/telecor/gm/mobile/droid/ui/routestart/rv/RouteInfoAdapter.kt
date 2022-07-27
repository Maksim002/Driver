package ru.telecor.gm.mobile.droid.ui.routestart.rv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_route.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.RouteStatusType
import ru.telecor.gm.mobile.droid.presentation.routestart.RouteStartPresenter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.routestart.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 03.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class RouteInfoAdapter(
    var listener: Listener,
    var fragment: Fragment,
    var presenter: RouteStartPresenter? = null,
    item: List<RouteInfo> = arrayListOf(),
    var list: ArrayList<LoaderInfo> = arrayListOf(),
) : GenericRecyclerAdapter<RouteInfo>(item) {
    private var first = MutableLiveData<LoaderInfo>()
    private var second = MutableLiveData<LoaderInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_route)
    }

    override fun bind(item: RouteInfo, holder: ViewHolder) = with(holder.itemView) {
        tvRegNumber.text = item.unit.vehicle.regNumber
        tvModelName.text = item.unit.vehicle.model
        val strDate = SimpleDateFormat("dd.MM.yyyy").format(Date(item.unit.plannedBeginTime))
        val strBeginTime = SimpleDateFormat("HH:mm").format(Date(item.unit.plannedBeginTime))
        val strEndTime = SimpleDateFormat("HH:mm").format(Date(item.unit.plannedEndTime))
        tvTime.text = "$strDate $strBeginTime-$strEndTime"

        if (item.status.name == RouteStatusType.INPROGRESS) {
            btnContinue.setBackgroundResource(R.drawable.circle_background_flooded_bottom)
            btnContinue.setColorFilter(
                ContextCompat.getColor(context, R.color.gmm_white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            btnContinue.setBackgroundResource(R.drawable.circle_background_bottom)
            btnContinue.setColorFilter(
                ContextCompat.getColor(context, R.color.blue_color),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        btnContinue.setOnClickListener(1000) {
            listener.clickLambdaListener(item)
        }

        if (item.status.name == RouteStatusType.INPROGRESS){
            spinnerObject(item, holder)
        }else{
            spinnerObject(item, holder)
        }
    }

    private fun spinnerObject(item: RouteInfo, holder: ViewHolder) = with(holder.itemView) {
        loaderFirstLayout.fragment = fragment
        loaderFirstLayout.presenter = presenter!!

        first.observe(fragment.viewLifecycleOwner) {
            if (presenter?.currentId != 0){
                if (item.id.toInt() == presenter?.currentId){
                    loaderFirstLayout.isEnabled = true
                    loaderFirstLayout.defaultUpdate(it)
                }else{
                    loaderFirstLayout.isEnabled = false
                }
            }else{
                loaderFirstLayout.isEnabled = true
                loaderFirstLayout.defaultUpdate(it)
            }
        }

        loaderFirstLayout.setOnClickListener(1000) {
            if (item.status.name == RouteStatusType.INPROGRESS ||
                item.status.name == RouteStatusType.CONFIRMED
            ) {
                loaderFirstLayout.update(list, 1)
            }
        }

        secondLoaderLayout.fragment = fragment
        secondLoaderLayout.presenter = presenter!!

            second.observe(fragment.viewLifecycleOwner) {
                if (presenter?.currentId != 0) {
                    if (item.id.toInt() == presenter?.currentId) {
                        secondLoaderLayout.isEnabled = true
                        secondLoaderLayout.defaultUpdate(it)
                    }else{
                        secondLoaderLayout.isEnabled = false
                    }
                }else{
                    secondLoaderLayout.isEnabled = true
                    secondLoaderLayout.defaultUpdate(it)
                }
            }

        secondLoaderLayout.setOnClickListener(1000) {
            if (item.status.name == RouteStatusType.INPROGRESS ||
                item.status.name == RouteStatusType.CONFIRMED
            ) {
                secondLoaderLayout.update(list, 2)
            }
        }
    }

    fun setFirstLoaderPreselected(loaderInfo: LoaderInfo) {
        first.value = loaderInfo
    }

    fun setSecondLoaderPreselected(loaderInfo: LoaderInfo) {
        second.value = loaderInfo
    }

    fun getListDown(listDow: List<LoaderInfo>) {
        list = listDow as ArrayList<LoaderInfo>
    }

    interface Listener {
        fun clickLambdaListener(item: RouteInfo)
    }
}
