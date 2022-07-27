package ru.telecor.gm.mobile.droid.presentation.garbageload.util

import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason
import ru.telecor.gm.mobile.droid.entities.ContainerLoadLevel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.garbageload.util
 *
 *
 *
 * Created by Emil Zamaldinov (aka Piligrim) 11.03.2021
 * Copyright © 2021 TKO-Inform. All rights reserved.
 */
data class GarbageLoadScreenState(
//    val pickupType: GarbageLoadScreenPickupState,
//    val listState: GarbageLoadScreenListState
    val levelsList: List<ContainerLoadLevel>? = null,
    val list: List<StatusTaskExtended>? = null,
    val localFailureReasonsCache: List<ContainerFailureReason>? = null
)
