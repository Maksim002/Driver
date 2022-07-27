package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 10.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class CompletedContainerInfo(
    @SerializedName("typeName")
    val typeName: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("volume")
    val volume: Double
)
