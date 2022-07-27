package ru.telecor.gm.mobile.droid.entities.request

import com.google.gson.annotations.SerializedName

data class PositionQuery(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
) {

    override fun toString(): String {
        return "{\"lat\":$lat,\"lng\":$lng}"
    }
}