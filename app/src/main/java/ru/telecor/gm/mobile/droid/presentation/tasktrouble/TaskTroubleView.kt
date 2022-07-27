package ru.telecor.gm.mobile.droid.presentation.tasktrouble

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.tasktrouble
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 21.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
interface TaskTroubleView : BaseView {

    @Skip
    fun takePhoto(routeCode: String, taskId: String)

    fun setPhotoPreview(path: String)

    fun setLoadingState(value: Boolean)

    fun setTaskAddress(string: String)

    fun setGeneralCan(localTaskCache: TaskExtended)

    fun setTroubleAdditionalInfo(time: Long, lat: Double, lon: Double)

    fun setTroubleDropdownList(list: List<TaskFailureReason>)

    fun setRecyclerData(list: List<StatusTaskExtended>? = null)

    @AddToEndSingle
    fun showPhoto(model: ArrayList<GarbagePhotoModel>)
}
