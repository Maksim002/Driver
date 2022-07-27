package ru.telecor.gm.mobile.droid.model.system

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.system
 *
 * Abstractions for resource managers (strings, images, etc.)
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface IResourceManager {
    fun getString(resId: Int): String

    fun getStringArray(resId: Int): Array<String>
}