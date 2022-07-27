package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Supported container types for a transport.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class SupportedContainerType(
    @SerializedName("containerType")
    val containerType: ContainerType,
    @SerializedName("actions")
    val actions: List<Action>
)