package ru.telecor.gm.mobile.droid.di.providers

import ru.telecor.gm.mobile.droid.model.data.db.dao.TourDao
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
class TourDaoProvider @Inject constructor(
    private val localDataPrefs: LocalDataPrefs
) : Provider<TourDao> {

    override fun get(): TourDao = localDataPrefs
}