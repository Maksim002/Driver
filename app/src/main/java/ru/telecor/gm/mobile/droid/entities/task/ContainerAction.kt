package ru.telecor.gm.mobile.droid.entities.task


import com.google.gson.annotations.SerializedName

data class ContainerAction(
    @SerializedName("name")
    val name: String,
    @SerializedName("caption")
    val caption: String
)