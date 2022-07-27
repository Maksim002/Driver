package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 19.04.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
data class CarryContainerHistoryItem(
    val carryContainer: CarryContainer,
    val containerActionType: ContainerHistoryActionType,
    val time: Long,
    val latitude: Double,
    val longitude: Double,
    val isSent: Boolean = false,
) {

    companion object {

        fun create(original: CarryContainerHistoryItemSendable) = CarryContainerHistoryItem(
            carryContainer = original.carryContainer,
            containerActionType = original.containerActionType,
            time = original.time,
            latitude = original.latitude,
            longitude = original.longitude,
            isSent = true
        )
    }
}

data class CarryContainerHistoryItemSendable(
    @SerializedName("container")
    val carryContainer: CarryContainer,
    @SerializedName("action")
    val containerActionType: ContainerHistoryActionType,
    @SerializedName("time")
    val time: Long,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
) {

    companion object {

        fun create(original: CarryContainerHistoryItem) = CarryContainerHistoryItemSendable(
            carryContainer = original.carryContainer,
            containerActionType = original.containerActionType,
            time = original.time,
            latitude = original.latitude,
            longitude = original.longitude
        )
    }
}

enum class ContainerHistoryActionType {
    @SerializedName("ADD")
    ADD,

    @SerializedName("REMOVE")
    REMOVE
}