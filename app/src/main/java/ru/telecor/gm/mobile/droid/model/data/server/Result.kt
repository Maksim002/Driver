package ru.telecor.gm.mobile.droid.model.data.server

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server
 *
 * Wrapper for server result.
 *
 * Created by Artem Skopincev (aka sharpyx) 30.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */

sealed class Result<out R : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}