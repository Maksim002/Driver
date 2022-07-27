package ru.telecor.gm.mobile.droid.presentation.serverSettings

import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ServerSettingsView : BaseView {

    fun setEmptyProtocolFieldError()
    fun setEmptyServerFieldError()
    fun setEmptyPortFieldError()
    fun setEmptyApplicationFieldError()

    fun showCurrentServerInfo(info: GmServerInfo)

    fun finishApp()
    fun finishActivity()
}
