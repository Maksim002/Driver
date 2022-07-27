package ru.telecor.gm.mobile.droid.presentation.containershistory

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.containershistory
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 23.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */

@InjectViewState
class ContainersHistoryPresenter @Inject constructor(
    val routeInteractor: RouteInteractor
) : BasePresenter<ContainersHistoryView>() {

    override fun attachView(view: ContainersHistoryView?) {
        super.attachView(view)
        val cchList = routeInteractor.getCarryContainersHistory()
        val ccList = routeInteractor.getCarryContainers()
        viewState.setHistoryList(cchList?.map { item ->
            Pair(
                item,
                ccList?.contains(item.carryContainer) ?: false
            )
        } ?: listOf())
    }
}
