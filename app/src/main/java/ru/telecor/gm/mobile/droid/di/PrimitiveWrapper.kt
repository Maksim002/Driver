package ru.telecor.gm.mobile.droid.di

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di
 *
 * Wrapper for primitive types.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class PrimitiveWrapper<out T>(val value: T) // see: https://youtrack.jetbrains.com/issue/KT-18918