package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.task.TaskType

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Customer info.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class Customer(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: TaskType,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortName")
    val shortName: String
)