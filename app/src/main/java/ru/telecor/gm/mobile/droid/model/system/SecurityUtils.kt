package ru.telecor.gm.mobile.droid.model.system

import android.util.Base64
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.system
 *
 * Utils for getting various signing and security strings, etc.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class SecurityUtils @Inject constructor() {

    companion object {
        const val StandardPassword = "1"
    }

    fun getBasicAuthHash(login: String, password: String): String {
        val authStr = "$login:$password"
        val bytes = authStr.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}