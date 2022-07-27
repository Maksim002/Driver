package ru.telecor.gm.mobile.droid.presentation.serverSettingsN

import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ServerSettingsBottomView: BaseView {
    fun setEmptyProtocolFieldError()
    fun setEmptyServerFieldError()
    fun setEmptyPortFieldError()
    fun setEmptyApplicationFieldError()

    fun showCurrentServerInfo(info: GmServerInfo)

    fun setCurrentVersion(version: String)

    fun setTextError(boolean: Boolean)

    fun isDismiss()
}
