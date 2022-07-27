package ru.telecor.gm.mobile.droid.entities

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 30.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class GmServerInfo(
    var protocol: String,
    var host: String,
    var port: Int,
    var application: String
)