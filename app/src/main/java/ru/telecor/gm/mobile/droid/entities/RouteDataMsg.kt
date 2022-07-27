package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.task.Simple
import ru.telecor.gm.mobile.droid.entities.task.Task
import ru.telecor.gm.mobile.droid.entities.task.TaskFull

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Ilimjan Baryktabasov (put in byte) 12.11.2021
 * Copyright © 2021 TKOInform. All rights reserved.
 */

data class RouteDataMsg(
    @SerializedName("routeId")
    val routeId : Long,
    @SerializedName("simpleList")
    val simpleList: List<Simple>? = null,
    @SerializedName("fullList")
    val fullList:  List<TaskFull>? = null,
    @SerializedName("fullList2")
    val fullList2: List<TaskFull>? = null,
    @SerializedName("relevanceTime")
    val relevanceTime: String
    )
