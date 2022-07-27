package ru.telecor.gm.mobile.droid.presentation.updateDialog

import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface UpdateDialogView : BaseView {

    fun setLoadingState(value: Boolean)

    fun switchingVersions(checkedVersion: BuildVersion)

    fun installAPK(path: String)

    fun installationAPK()
}
