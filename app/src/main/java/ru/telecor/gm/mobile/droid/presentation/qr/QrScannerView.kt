package ru.telecor.gm.mobile.droid.presentation.qr

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.entities.ContainerHistoryActionType
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.qr
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 16.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
interface QrScannerView : BaseView {

    fun setDialogVisible(value: Boolean, carryContainerHistoryItem: CarryContainerHistoryItem?)

    @Skip
    fun showActionCompletedMessage(actionType: ContainerHistoryActionType)

    @Skip
    fun showErrorDialog(message: String)

}