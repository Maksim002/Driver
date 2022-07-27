package ru.telecor.gm.mobile.droid.di.providers

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.telecor.gm.mobile.droid.di.ServerPath
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApiLongRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class ApiProviderLongRequest @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
    @ServerPath private val serverPath: String,
) : Provider<TruckCrewApiLongRequest> {

    override fun get(): TruckCrewApiLongRequest {

        val newOkHttpClient = okHttpClient.newBuilder()
            .connectTimeout(150L, TimeUnit.SECONDS)
            .writeTimeout(150L, TimeUnit.SECONDS)
            .readTimeout(150L, TimeUnit.SECONDS)
            .build()

        val url = serverPath + "mobile/"

        return Retrofit.Builder()
            .baseUrl(url)
            .client(newOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TruckCrewApiLongRequest::class.java)
    }
}