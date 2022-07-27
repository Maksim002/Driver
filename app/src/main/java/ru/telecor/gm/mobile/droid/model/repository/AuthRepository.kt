package ru.telecor.gm.mobile.droid.model.repository

import kotlinx.coroutines.Dispatchers
import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.LoginRequest
import ru.telecor.gm.mobile.droid.entities.TourInfo
import ru.telecor.gm.mobile.droid.model.data.db.dao.TourDao
import ru.telecor.gm.mobile.droid.model.data.server.GmWebSocket
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.storage.AppPreferences
import ru.telecor.gm.mobile.droid.model.repository.mod.OptionsModel
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.repository
 *
 * User authorization repository.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class AuthRepository @Inject constructor(
    private val truckCrewApi: TruckCrewApi,
    private val  appPreferences: AppPreferences,
    private val tourDao: TourDao
) : BaseRepository() {

    private var tourInfoCache: TourInfo? = null
    private var driverInfoCache: DriverInfo? = null

    // region Fields
    val tourInfo: TourInfo?
        get() = if (tourInfoCache != null) {
            tourInfoCache
        } else {
            tourInfoCache = tourDao.getTourInfo()
            tourInfoCache
        }
    // endregion

    suspend fun login(loginData: LoginRequest): Result<TourInfo> = safeApiCall(Dispatchers.IO) {
        val tourInfo = truckCrewApi.login(loginData)

        tourInfoCache = tourInfo
        driverInfoCache = tourInfo.driver
        tourDao.setTourInfo(tourInfo)

        val t = tourDao.getTourInfo()

        return@safeApiCall tourInfo
    }

    suspend fun options(): Result<OptionsModel> = safeApiCall(Dispatchers.IO) {
        val tourInfo = truckCrewApi.options()
        appPreferences.photoPeriodDelete = tourInfo.photoPeriodDelete!!.toInt()
        appPreferences.reloadDeep = tourInfo.reloadDeep!!.toInt()
        appPreferences.photoHeight = tourInfo.photoSize!!.height!!.toString()
        appPreferences.photoWidth = tourInfo.photoSize!!.width!!.toString()
        return@safeApiCall tourInfo
    }

    fun logOut() {
        tourInfoCache = null
        tourDao.setTourInfo(null)
        tourDao.setCurrentRoute(null)
    }
}
