package ru.telecor.gm.mobile.droid.presentation.photoMake

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.presentation.base.BaseView
import java.io.File

interface PhotoMakeView : BaseView {

    fun setTempFile(file: File)

    fun takePhoto(isFlashEnabled: Boolean = false)

    fun setCurrentFlashState(isFlashEnabled: Boolean)

    fun showPreviewDialog(value: Boolean)

    fun setResultAndFinish()

    @Skip
    fun back()
}
