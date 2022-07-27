package ru.telecor.gm.mobile.droid.entities.request

import ru.telecor.gm.mobile.droid.entities.LoaderInfo

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.request
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 07.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class PorterData(var porterNumber: Int, val staffId: String?) {

    companion object {
        fun create(num: Int, loader: LoaderInfo?) = PorterData(num, loader?.id)
    }
}
