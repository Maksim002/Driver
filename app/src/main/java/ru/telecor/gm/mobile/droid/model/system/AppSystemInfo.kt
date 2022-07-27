package ru.telecor.gm.mobile.droid.model.system

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.system
 *
 * App system info.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class AppSystemInfo(
    var name: String,
    var versionName: String,
    var versionCode: Int,
    var deviceId: String
)