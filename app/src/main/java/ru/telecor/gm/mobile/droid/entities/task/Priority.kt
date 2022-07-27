package ru.telecor.gm.mobile.droid.entities.task

import com.google.gson.annotations.SerializedName

data class Priority(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: Int
)