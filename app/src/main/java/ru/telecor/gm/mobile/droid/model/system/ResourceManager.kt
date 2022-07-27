package ru.telecor.gm.mobile.droid.model.system

import android.content.Context
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.system
 *
 * Implementation of resource manager.
 * Can get string by default case from Android OS.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ResourceManager @Inject constructor(private val context: Context) : IResourceManager {

    override fun getString(resId: Int): String = context.getString(resId)

    override fun getStringArray(resId: Int): Array<String> = context.resources.getStringArray(resId)
}