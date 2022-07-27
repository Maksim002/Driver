package ru.telecor.gm.mobile.droid.di.providers

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.telecor.gm.mobile.droid.di.ServerPath
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.utils.LogUtils
import javax.inject.Inject
import javax.inject.Provider

class ApiProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
    @ServerPath private val serverPath: String,
) : Provider<TruckCrewApi> {

    override fun get(): TruckCrewApi {

        val url = serverPath + "mobile/"

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TruckCrewApi::class.java)
    }
}