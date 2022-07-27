package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 30.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class GmSocketResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("unitId")
    val unitId: String
)