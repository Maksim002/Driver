package ru.telecor.gm.mobile.droid.presentation.phototrouble

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface PhotoTroubleView : BaseView {

    fun showListOfPhotos(list: List<ProcessingPhoto>)

    fun showListOfBlockagePhotos(list: List<ProcessingPhoto>)

    @Skip
    fun setResultAndFinish(path: String)

    @Skip
    fun cancel()

    @Skip
    fun startInternalCameraForResult(path: String)

    @Skip
    fun startExternalCameraForResult(path: String)

    @Skip
    fun back()
}
