package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 25.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class WebSocketMsg(
    @SerializedName("unitId")
    val unitId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("command")
    val command: WebSocketCommand
)