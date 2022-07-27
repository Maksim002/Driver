package ru.telecor.gm.mobile.droid.model.interactors

import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.LoginRequest
import ru.telecor.gm.mobile.droid.entities.TourInfo
import ru.telecor.gm.mobile.droid.model.data.server.GmWebSocket
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.repository.AuthRepository
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.repository.RouteRepository
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 * Interactor for authorization in app.
 * Use cases:
 * - Login in system
 * - Check is logged in
 * - Logout from system
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository,
    private val routeRepository: RouteRepository,
    private val commonDataRepository: CommonDataRepository,
    private val authHolder: AuthHolder
) {

    val driverInfo: DriverInfo?
        get() = authRepository.tourInfo?.driver

    suspend fun login(personnelNum: String, loginData: LoginRequest, androidVersion: String, codename: String ,device: String): Result<TourInfo> {
        // set info to auth holder (shared pref's)
        authHolder.personnelNumber = personnelNum
        loginData.platformVersion = androidVersion
        loginData.codename = codename
        loginData.device = device
        return login(loginData)
    }

    suspend fun login(loginData: LoginRequest): Result<TourInfo> {

        // get data for caching for offline-mode

        // TODO wsChange нужно поставить условие если роут первоначальный или слабый имнтерен не запрашивать эти данные
        if (routeRepository.startedRoute == null){
            commonDataRepository.getVisitPoints()
            commonDataRepository.getAvailablePorters(true)
            authRepository.options()
        }
        return authRepository.login(loginData)
    }

    fun isLoggedIn(): Boolean = authRepository.tourInfo != null

    fun logout() {
        authRepository.logOut()
        routeRepository.clearAllCache()
        commonDataRepository.clearAllCache()
    }
}
