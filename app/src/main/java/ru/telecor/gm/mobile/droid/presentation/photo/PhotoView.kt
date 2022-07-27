package ru.telecor.gm.mobile.droid.presentation.photo

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.photo
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 29.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface PhotoView : BaseView {

    fun showListOfPhotos(list: List<ProcessingPhoto>)

    @Skip
    fun setResultAndFinish(path: String)

    @Skip
    fun cancel()

    fun setAppBarTitle(title: String)

    @Skip
    fun photoWithGPSWanted()

    @Skip
    fun startInternalCameraForResult(path: String)

    @Skip
    fun startExternalCameraForResult(path: String)

    @Skip
    fun back()
}
