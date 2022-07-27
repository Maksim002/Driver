package ru.telecor.gm.mobile.droid.presentation.login

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

@AddToEndSingle
interface LoginView : BaseView {

    fun setPersonnelNumberFieldEmptyError()
    fun setLoadingState(value: Boolean)

    @Skip
    fun openMainScreen()

    @Skip
    fun openAboutScreen()

    @Skip
    fun openSettingsScreen()

    @Skip
    fun showErrorMessage(ex: Throwable)

    @Skip
    fun showErrorMessage(message: String)

    @Skip
    fun showErrorDialog(ex: Throwable)

    @Skip
    fun showConfirmPasswordDialog()

    @Skip
    fun showUpdateDialog(version: BuildVersion)

    fun setCurrentVersion(version: String)

    fun setCheckingVersion(versionPhone: String, versionServer: String, boolean: Boolean,  int: Int, isInCom: Boolean)
}
