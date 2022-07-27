package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Info for requesting Login method.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class LoginRequest(
    @SerializedName("gpsSTime")
    val gpsTime: Long,
    @SerializedName("imei")
    val imei: String,
    @SerializedName("imsi")
    val imsi: String?,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("time")
    val time: Long,
    @SerializedName("version")
    val version: VersionData,
    @SerializedName("codename")
    var codename: String? = null,
    @SerializedName("device")
    var device: String? = null,
    @SerializedName("platformVersion")
    var platformVersion: String? = null,
)