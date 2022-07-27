package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Route status.
 * TODO: describe statuses
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class RouteStatus(
    @SerializedName("name")
    val name: RouteStatusType,
    @SerializedName("caption")
    val caption: String
)
