package ru.telecor.gm.mobile.droid.entities.request

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.request
 *
 * Entity for finishing route.
 *
 * Created by Emil Zamaldinov (aka piligrim) 07.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class FinishRouteData(
    @SerializedName("valid")
    var valid: Boolean,
    @SerializedName("latitude")
    var lat: Double?,
    @SerializedName("longitude")
    var lon: Double?
)