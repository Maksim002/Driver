package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

enum class StatusType(val order: Int) {
    @SerializedName("NEW")
    NEW(2),
    @SerializedName("SUCCESS")
    SUCCESS(1),
    @SerializedName("PARTLY")
    PARTIALLY(1),
    @SerializedName("FAIL")
    FAIL(1)
}


