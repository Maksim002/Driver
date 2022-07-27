package ru.telecor.gm.mobile.droid.model.data.server

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server
 *
 * Entity which returns by server when error occurred.
 *
 * Created by Artem Skopincev (aka sharpyx) 06.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class ApiErrorResponse(
    @SerializedName("message")
    val message: String
)