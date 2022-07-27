package ru.telecor.gm.mobile.droid.presentation.settingFunctionality

import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface SettingBottomSheetView : BaseView {
    fun setCurrentVersion(version: String)
    fun visibilityNext(boolean: Int)
}
