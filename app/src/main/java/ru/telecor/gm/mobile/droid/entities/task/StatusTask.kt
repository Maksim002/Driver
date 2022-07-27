package ru.telecor.gm.mobile.droid.entities.task


import com.google.gson.annotations.SerializedName

data class StatusTask(
    @SerializedName("id")
    val id: Int,
    @SerializedName("containerTypeId")
    val containerTypeId: String,
    @SerializedName("taskItemId")
    val taskItemId: Int
)