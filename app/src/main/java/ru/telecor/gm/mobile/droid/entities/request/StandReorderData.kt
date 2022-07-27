package ru.telecor.gm.mobile.droid.entities.request

import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.request
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 07.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class StandReorderData(
    val nextPointId: Long,
    val time: Date? = null,
    val plannedPointId: Long? = null,
    val actualOrder: Int? = null,
    val online: Boolean? = null
)