package ru.telecor.gm.mobile.droid.entities.request

import com.google.gson.annotations.SerializedName

data class PolygonRequest(
    @SerializedName("visitPointId")
    val visitPointId: String
)
