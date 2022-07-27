package ru.telecor.gm.mobile.droid.model.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
class DeviceLocationProvider(
    private var context: Context
) : LocationProvider {

    override fun getCurrentLocation(): LocationInfo? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)
        var bestLoc: Location? = null

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            FirebaseCrashlytics.getInstance()
                .log("getting permissions from DeviceLocationProvider")
            throw Exception("permissions for device locations was not granted")
        }

        for (p in providers) {
            val l = lm.getLastKnownLocation(p) ?: continue

            if (bestLoc == null || l.accuracy < bestLoc.accuracy) {
                bestLoc = l
            }
        }

        if (bestLoc == null) return null

        return LocationInfo(bestLoc.longitude, bestLoc.latitude)
    }
}