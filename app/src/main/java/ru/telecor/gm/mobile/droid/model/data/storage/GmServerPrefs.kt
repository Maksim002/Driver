package ru.telecor.gm.mobile.droid.model.data.storage

import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.model.BuildCon

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.storage
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 30.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface GmServerPrefs {
    fun getGmBuildCon(): BuildCon
    fun setGmBuildCon(info: BuildCon)

    fun getGmServerInfo(): GmServerInfo?
    fun setGmServerInfo(info: GmServerInfo)
}