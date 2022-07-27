package ru.telecor.gm.mobile.droid.entities.processing

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.db.PhotoProcessingForApi

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.processing
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 17.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class StandResult(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("arrivalTime")
    val arrivalTime: Long,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("time")
    val time: Long,
    @SerializedName("containerStatuses")
    var containerStatuses: List<ContainerStatus>,
    @SerializedName("photosCount")
    val photosCount: Int,
    @SerializedName("photos")
    val photos: List<PhotoProcessingForApi>? = null,
    @SerializedName("tonnage")
    val tonnage: Int?,
    @SerializedName("stand")
    val stand: Boolean = true
)