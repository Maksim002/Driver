package ru.telecor.gm.mobile.droid.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.utils
 *
 * Utils for getting location info, etc.
 *
 * Created by Artem Skopincev (aka sharpyx) 04.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getBestLocation(context: Context): Location? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)
        var bestLoc: Location? = null

        for (p in providers) {
            val l = lm.getLastKnownLocation(p) ?: continue

            if (bestLoc == null || l.accuracy < bestLoc.accuracy) {
                bestLoc = l
            }
        }

        return bestLoc
    }
}