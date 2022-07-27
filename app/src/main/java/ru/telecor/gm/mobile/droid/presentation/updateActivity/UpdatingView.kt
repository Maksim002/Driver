package ru.telecor.gm.mobile.droid.presentation.updateActivity

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

@AddToEndSingle
interface UpdatingView  : BaseView {

    fun setLoadingState(isUpdating: Boolean, version: BuildVersion? = null)

    fun switchingVersions(checkedVersion: BuildVersion)

    fun installAPK(path: String)

    fun installationAPK()

    fun setNewestVersion(newestVersion: String, version: BuildVersion)

    fun showUpdateDialog(version: BuildVersion)
}