package ru.telecor.gm.mobile.droid.di.providers

import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import timber.log.Timber
import java.lang.Exception
import java.net.URL
import javax.inject.Inject
import javax.inject.Provider

class ServerUrlProvider @Inject constructor(
    private val gmServerPrefs: GmServerPrefs
) : Provider<String> {

    override fun get(): String {
        var info = gmServerPrefs.getGmServerInfo()
        val defaultInfo = GmServerInfo(
            BuildConfig.SERVER_PROTOCOL,
            BuildConfig.SERVER_HOST,
            BuildConfig.SERVER_PORT.toInt(),
            BuildConfig.SERVER_APPLICATION
        )

        if (info == null) {
            gmServerPrefs.setGmServerInfo(defaultInfo)
            info = defaultInfo
        }

        return try {
            getUrlFromServerInfo(info)
        } catch (e: Exception) {
            Timber.e("Не удалось сформировать URL. Беру дефолтные данные")
            Timber.e(e)
            gmServerPrefs.setGmServerInfo(defaultInfo)
            getUrlFromServerInfo(defaultInfo)
        }
    }

    fun getUrlFromServerInfo(info: GmServerInfo): String {
        return if (info.port != 0) {
            URL(info.protocol, info.host, info.port, info.application).toString() + "/"
        } else {
            URL(info.protocol, info.host, info.application).toString() + "/"
        }
    }
}