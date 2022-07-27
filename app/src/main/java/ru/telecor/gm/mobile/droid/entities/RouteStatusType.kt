package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

enum class RouteStatusType {
    @SerializedName("PLAN")
    PLAN,

    @SerializedName("CONFIRMED")
    CONFIRMED,

    @SerializedName("INPROGRESS")
    INPROGRESS,

    @SerializedName("BROKEN")
    BROKEN,

    @SerializedName("FINISHED")
    FINISHED,

    @SerializedName("STATED")
    STATED
}