package ru.telecor.gm.mobile.droid.model.system

import ru.telecor.gm.mobile.droid.model.entities.LocationInfo

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.system
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 12.11.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface LocationProvider {

    fun getCurrentLocation(): LocationInfo?
}