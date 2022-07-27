package ru.telecor.gm.mobile.droid.entities.processing

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.StatusType

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.processing
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 17.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class ProcessingStatusType(
    @SerializedName("caption")
    val caption: String,
    @SerializedName("name")
    val name: StatusType
)