package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.model.data.server.CommandActionEnum

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 25.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class WebSocketCommand(
    @SerializedName("type")
    val type: String,
    @SerializedName("action")
    val action: CommandActionEnum? = null,
    @SerializedName("data")
    val data: String
)