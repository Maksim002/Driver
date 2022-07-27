package ru.telecor.gm.mobile.droid.model.data.db.dao

import ru.telecor.gm.mobile.droid.entities.CarryContainer
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.TourInfo

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.db.dao
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface TourDao {

    fun getTourInfo(): TourInfo?
    fun setTourInfo(tourInfo: TourInfo?)

    fun setCurrentRoute(routeInfo: RouteInfo?)
    fun getCurrentRoute(): RouteInfo?

    fun setCarryContainers(list: List<CarryContainer>?)
    fun getCarryContainers(): List<CarryContainer>?

    fun setCarryContainersHistory(list: List<CarryContainerHistoryItem>?)
    fun getCarryContainersHistory(): List<CarryContainerHistoryItem>?
}