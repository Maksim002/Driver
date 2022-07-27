package ru.telecor.gm.mobile.droid.presentation.updateVersion

import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface UpdatingVersionSheetView : BaseView {
    fun setLoadingState(isUpdating: Boolean, version: BuildVersion)

    fun setCurrentVersion(version: String)

    fun setNewestVersion(newestVersion: String, version: BuildVersion)

    fun finishScreen()

    fun showUpdateDialog(version: BuildVersion)

    fun installAPK(path: String)
}
