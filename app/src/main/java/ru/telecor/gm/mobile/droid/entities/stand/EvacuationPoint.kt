package ru.telecor.gm.mobile.droid.entities.stand


import com.google.gson.annotations.SerializedName

data class EvacuationPoint(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("routeToRoad")
    val routeToRoad: String?,
    @SerializedName("geoJson")
    val geoJson: String?
)
