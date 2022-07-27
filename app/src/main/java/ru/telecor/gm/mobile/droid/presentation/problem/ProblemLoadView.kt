package ru.telecor.gm.mobile.droid.presentation.problem

import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ProblemLoadView : BaseView {

    fun initRecyclerView(item: ArrayList<GarbagePhotoModel>)

    @Skip
    fun startExternalCameraForResult(path: String)
}
