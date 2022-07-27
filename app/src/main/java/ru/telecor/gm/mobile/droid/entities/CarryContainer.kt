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
data class CarryContainer(
    @SerializedName("value")
    val id: String,
    @SerializedName("type")
    val type: String = "RFID"
)
