package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Information about a route.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class RouteInfo(
    @SerializedName("id")
    val id: Long,
    @SerializedName("unit")
    val unit: CrewUnit,
    @SerializedName("date")
    val date: Long,
    @SerializedName("status")
    val status: RouteStatus,
    @SerializedName("allowCreatingTasks")
    val allowCreatingTasks: Boolean,
    @SerializedName("usesTablet")
    val usesTablet: Boolean,
    @SerializedName("requirePhotoBefore")
    val requirePhotoBefore: Boolean,
    @SerializedName("requirePhotoAfter")
    val requirePhotoAfter: Boolean,
    @SerializedName("requireFailurePhoto")
    val requireFailurePhoto: Boolean
)