package ru.telecor.gm.mobile.droid.utils

import android.util.Base64
import timber.log.Timber
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.05.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */
object SecurityUtils {

    // Формирует подпись с помощью ключа.
    fun sha256rsa(key: String, data: String): String? {
        val trimmedKey = key.replace("-----\\w+ PRIVATE KEY-----".toRegex(), "")
            .replace("\\s".toRegex(), "")
        return try {
            val result: ByteArray = Base64.decode(trimmedKey, Base64.DEFAULT)
            val factory: KeyFactory = KeyFactory.getInstance("RSA")
            val keySpec: EncodedKeySpec = PKCS8EncodedKeySpec(result)
            val signature: Signature = Signature.getInstance("SHA256withRSA")
            signature.initSign(factory.generatePrivate(keySpec))
            signature.update(data.toByteArray())
            val encrypted: ByteArray = signature.sign()
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            Timber.e("TST %s", e.message)
            throw SecurityException("Error calculating cipher data. SIC!")
        }
    }

}