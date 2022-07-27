package ru.telecor.gm.mobile.droid.presentation.qr

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.entities.CarryContainer
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.entities.ContainerHistoryActionType
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.qr
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 16.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
@InjectViewState
class QrScannerPresenter @Inject constructor(
    val routeInteractor: RouteInteractor,
    val router: Router
) : BasePresenter<QrScannerView>() {

    fun onCodeScanned(id: String) {

        try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            viewState.showErrorDialog("Недопустимый формат")
            return
        }

        val list = routeInteractor.getCarryContainers() ?: listOf()
        val container = CarryContainer(id)
        if (list.map { item -> item.id.toLowerCase() }.contains(id.toLowerCase())) {
            viewState.setDialogVisible(
                true,
                CarryContainerHistoryItem(
                    container,
                    ContainerHistoryActionType.REMOVE,
                    Date().time,
                    0.0,
                    0.0
                )
            )
        } else {
            viewState.setDialogVisible(
                true,
                CarryContainerHistoryItem(
                    container,
                    ContainerHistoryActionType.ADD,
                    Date().time,
                    0.0,
                    0.0
                )
            )
        }
    }

    fun onActionAccepted(containerHistoryItem: CarryContainerHistoryItem) {
        viewState.setDialogVisible(false, null)
        when (containerHistoryItem.containerActionType) {
            ContainerHistoryActionType.ADD -> {
                routeInteractor.addCarryContainer(
                    containerHistoryItem.carryContainer
                )
            }
            ContainerHistoryActionType.REMOVE -> {
                routeInteractor.removeCarryContainer(
                    containerHistoryItem.carryContainer
                )
            }
        }
        viewState.showActionCompletedMessage(containerHistoryItem.containerActionType)
    }

    fun onActionCancelled() {
        viewState.setDialogVisible(false, null)
    }
}
