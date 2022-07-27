package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Information about latest version of the app from server.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class AppVersionResponse(
    @SerializedName("major")
    val major: Int,
    @SerializedName("minor")
    val minor: Int,
    @SerializedName("build")
    val build: Int
)