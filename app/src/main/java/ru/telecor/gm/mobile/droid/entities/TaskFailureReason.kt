package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Reason of task exporting failure.
 *
 * Created by Emil Zamaldinov (aka piligrim) 20.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class TaskFailureReason(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("changeTime")
    val changeTime: Long
)