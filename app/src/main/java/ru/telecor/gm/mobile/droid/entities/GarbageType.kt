package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Type of garbage for export.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class GarbageType(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)