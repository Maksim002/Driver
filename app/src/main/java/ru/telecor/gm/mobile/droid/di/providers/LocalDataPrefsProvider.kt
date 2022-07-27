package ru.telecor.gm.mobile.droid.di.providers

import android.content.Context
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.model.data.storage.LocalDataPrefs
import javax.inject.Inject
import javax.inject.Provider

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.providers
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class LocalDataPrefsProvider @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : Provider<LocalDataPrefs> {

    override fun get(): LocalDataPrefs {
        return LocalDataPrefs(context, gson)
    }
}