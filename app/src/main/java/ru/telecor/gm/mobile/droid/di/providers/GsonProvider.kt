package ru.telecor.gm.mobile.droid.di.providers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javax.inject.Inject
import javax.inject.Provider

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.providers
 *
 * GSON provider.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class GsonProvider @Inject constructor() : Provider<Gson> {

    override fun get(): Gson = GsonBuilder().create()
}