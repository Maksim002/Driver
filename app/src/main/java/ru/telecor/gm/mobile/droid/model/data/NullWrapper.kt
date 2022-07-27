package ru.telecor.gm.mobile.droid.model.data

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data
 *
 * Null wrapper for DI (Toothpick).
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class NullWrapper<T>(
    val data: T?
)