package ru.telecor.gm.mobile.droid.entities.processing

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.processing
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 23.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class
ContainerStatusType(
    @SerializedName("caption")
    val caption: String,
    @SerializedName("name")
    val name: Type
) {
    enum class Type {
        @SerializedName("new")
        NEW,
        @SerializedName("success")
        SUCCESS,
        @SerializedName("partly")
        PARTIALLY,
        @SerializedName("failed")
        FAILED
    }
}