package ru.telecor.gm.mobile.droid.presentation.tasktrouble.problem

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ProblemView : BaseView {

    fun setMandatoryPhoto(value: Boolean)

    fun defaultValue(name: String)

    @Skip
    fun startExternalCameraForResult(path: String)

    @AddToEndSingle
    fun showPhoto(model: ArrayList<GarbagePhotoModel>)
}
