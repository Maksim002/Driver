package ru.telecor.gm.mobile.droid.model.data.server.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.SecurityUtils
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server.interceptors
 *
 * Interceptor for signing requests to server.
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class SigningInterceptor @Inject constructor(
    private val securityUtils: SecurityUtils,
    private val authHolder: AuthHolder
) : Interceptor {

    private val password = "1"

    override fun intercept(chain: Interceptor.Chain): Response {
        // We have to add basic auth to each request to server.
        // Basic auth contains login (number of a driver) and password (1).
        // 1 - is strongly constant in server, use this all time.

        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
        val pn = authHolder.personnelNumber ?: return chain.proceed(newRequest.build())

        val basicAuthHash = securityUtils.getBasicAuthHash(pn, password).replace("\n", "")

        newRequest.addHeader("Authorization", "Basic $basicAuthHash")
        newRequest.removeHeader("Accept-Encoding")

        return chain.proceed(newRequest.build())
    }
}