package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Information about shift.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class CrewUnit(
    @SerializedName("id")
    val id: String,
    @SerializedName("vehicle")
    val vehicle: Vehicle,
    @SerializedName("driver")
    val driver: DriverInfo,
    @SerializedName("loader")
    var loader: LoaderInfo?,
    @SerializedName("secondLoader")
    val secondLoader: LoaderInfo?,
    @SerializedName("plannedBeginTime")
    val plannedBeginTime: Long,
    @SerializedName("plannedEndTime")
    val plannedEndTime: Long,
    @SerializedName("beginTime")
    val beginTime: Long,
    @SerializedName("allowedPolygonIds")
    val allowedPolygonIds: List<String>,
    @SerializedName("managed")
    val managed: List<Any>
)
