package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.stand.EvacuationPoint

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Visit point.
 *
 * Created by Artem Skopincev (aka sharpyx) 28.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class VisitPoint(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("evacuationPointId")
    val evacuationPointId: String,
    @SerializedName("evacuationPoint")
    val evacuationPoint: EvacuationPoint,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("finishTime")
    val finishTime: Long?,
    @SerializedName("pointType")
    val pointType: VisitPointType
)