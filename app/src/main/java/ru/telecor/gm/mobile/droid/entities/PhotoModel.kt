package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

class PhotoModel (
    @SerializedName("content")
    val content: String? = "",
    @SerializedName("contentType")
    val contentType: String? = null
)
