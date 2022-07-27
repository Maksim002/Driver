package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.GarbageType
import ru.telecor.gm.mobile.droid.entities.SupportedContainerType

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Info about vehicle.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class Vehicle(
    @SerializedName("id")
    val id: String,
    @SerializedName("regNumber")
    val regNumber: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("supportedContainerTypes")
    val supportedContainerTypes: List<SupportedContainerType>,
    @SerializedName("supportedGarbageTypes")
    val supportedGarbageTypes: List<GarbageType>,
    @SerializedName("containers")
    val containers: List<Any>
)