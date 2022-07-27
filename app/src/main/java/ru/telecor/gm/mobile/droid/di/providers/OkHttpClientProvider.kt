package ru.telecor.gm.mobile.droid.di.providers

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.data.server.interceptors.CurlLoggingInterceptor
import ru.telecor.gm.mobile.droid.model.data.server.interceptors.SigningInterceptor
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.SecurityUtils
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.providers
 *
 * Provides OkHttpClient instance.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class OkHttpClientProvider @Inject constructor(
    private val securityUtils: SecurityUtils,
    private val authHolder: AuthHolder
) : Provider<OkHttpClient> {

    private val connectTimeout = 30L
    private val readTimeout = 30L
    private val writeTimeout = 30L

    override fun get(): OkHttpClient = with(OkHttpClient.Builder()) {
        connectTimeout(connectTimeout, TimeUnit.SECONDS)
        writeTimeout(writeTimeout, TimeUnit.SECONDS)
        readTimeout(readTimeout, TimeUnit.SECONDS)

        // interceptor for signing requests to server
        addNetworkInterceptor(SigningInterceptor(securityUtils, authHolder))
//        addNetworkInterceptor(ErrorInterceptor())

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (!message.contains("�")) {
                        Timber.d(message)
                    }
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addNetworkInterceptor(CurlLoggingInterceptor())
        }

        build()
    }
}