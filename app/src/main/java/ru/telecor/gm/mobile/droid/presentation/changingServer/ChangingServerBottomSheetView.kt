package ru.telecor.gm.mobile.droid.presentation.changingServer

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ChangingServerBottomSheetView: BaseView {
    fun setCurrentVersion(version: String)

    @Skip
    fun showConfirmPasswordDialog()

    @Skip
    fun openSettingsScreen()

}
