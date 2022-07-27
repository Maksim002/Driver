package ru.telecor.gm.mobile.droid.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 21.06.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */

fun tickerFlow(period: Long, initialDelay: Long = 0) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}