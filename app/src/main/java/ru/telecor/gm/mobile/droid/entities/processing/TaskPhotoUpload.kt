package ru.telecor.gm.mobile.droid.entities.processing

import ru.telecor.gm.mobile.droid.model.PhotoType
import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.processing
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 22.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class TaskPhotoUpload(
    val id: UUID,
    val evacuationPointTaskId: Long,
    val routeTaskId: Long,
    val containerStatusId: Long,
    val actualReason: String,
    val latitude: Long,
    val longitude: Long,
    val photoType: PhotoType,
    val createTime: Long,
    val delivered: Boolean
)