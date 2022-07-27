package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Type of visit point.
 *
 * Created by Artem Skopincev (aka sharpyx) 28.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class VisitPointType(
    @SerializedName("name")
    val name: Type,
    @SerializedName("caption")
    val caption: String
) {
    enum class Type {
        @SerializedName("PARKING") Parking,
        @SerializedName("FUELING") Fueling,
        @SerializedName("RECYCLING") Recycling,
        @SerializedName("EMPTY") Empty,
        @SerializedName("PORTAL") Portal
    }
}