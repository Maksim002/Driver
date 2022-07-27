package ru.telecor.gm.mobile.droid.model.data.server.interceptors

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import ru.telecor.gm.mobile.droid.model.data.server.ApiErrorResponse
import ru.telecor.gm.mobile.droid.model.data.server.ServerError

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server.interceptors
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 06.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code in 400..500) {
            val errorResponse: ApiErrorResponse = if (response.body != null) {
                val body = response.body!!.string()
                Gson().fromJson<ApiErrorResponse>(body,
                    ApiErrorResponse::class.java)
            } else {
                ApiErrorResponse("bad request")
            }

            throw ServerError(response.code, errorResponse)
        }

        return response
    }
}