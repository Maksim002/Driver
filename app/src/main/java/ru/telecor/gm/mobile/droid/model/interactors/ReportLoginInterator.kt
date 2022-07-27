package ru.telecor.gm.mobile.droid.model.interactors

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.model.system.reports.LoginStateReport
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.lang.Exception
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 20.05.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ReportLoginInterator @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val authHolder: AuthHolder,
    private val context: Context
) {

    companion object {
        private const val REPORT_ROUTE_ID = "REPORT_ROUTE_ID"

        private const val REPORT_DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val REPORT_DRIVER_NAME = "REPORT_DRIVER_NAME"
        private const val REPORT_GENERATE_TIME = "REPORT_GENERATE_TIME"

        private const val REPORT_CURRENT_INTERNET_SPEED = "REPORT_CURRENT_INTERNET_SPEED"
        private const val REPORT_ALLOWED_INTERNET_SPEED = "REPORT_ALLOWED_INTERNET_SPEED"
        private const val REPORT_APK_INSTALLATION_DATE = "REPORT_APK_INSTALLATION_DATE"
        private const val REPORT_DATE_LAST_APK_UPDATE = "REPORT_DATE_LAST_APK_UPDATE"
        private const val REPORT_INTERNET_INFO = "REPORT_INTERNET_INFO"


        private const val NullValue = "Значение не установлено"
    }

    fun getContext() = context

    var TAG = javaClass.simpleName

    suspend fun sendReport(personalNumber: String) {
        val driver = authInteractor.driverInfo
        val defaultInternetInfo = "Не определено"

        val internetSpeed = try {
            ConnectivityUtils.getNetworkUpSpeed(context)
        } catch (e: Exception) {
            defaultInternetInfo
        }

        val allowedInternetSpeed = try {
            ConnectivityUtils.isConnectedFast(context)
        } catch (e: Exception) {
            defaultInternetInfo
        }

        val internetConnectInfo = try {
            ConnectivityUtils.getNetworkInfo(context)
        } catch (e: Exception) {
            defaultInternetInfo
        }

        val dateInstall = Date(context.packageManager.getPackageInfo( context.packageName, 0 ).firstInstallTime)
        val dateLastUpdate = Date(context.packageManager.getPackageInfo( context.packageName, 0 ).lastUpdateTime)


        val gson = Gson()
        val driverName = if (driver != null) {
            driver.lastName + driver.firstName + driver.middleName
        } else {
            NullValue
        }

        FirebaseCrashlytics.getInstance().run {
            LogUtils.error(TAG, "888888888888888888888888888888")

            setCustomKey(REPORT_DRIVER_NUMBER, personalNumber)
            setCustomKey(REPORT_DRIVER_NAME, driverName)
            setCustomKey(REPORT_GENERATE_TIME, Date().time.toString())

            setCustomKey(REPORT_CURRENT_INTERNET_SPEED, gson.toJson(internetSpeed))
            setCustomKey(REPORT_ALLOWED_INTERNET_SPEED, gson.toJson(allowedInternetSpeed))
            setCustomKey(REPORT_APK_INSTALLATION_DATE,dateInstall.toString())
            setCustomKey(REPORT_DATE_LAST_APK_UPDATE, dateLastUpdate.toString())
            setCustomKey(REPORT_INTERNET_INFO, gson.toJson(internetConnectInfo))

            recordException(LoginStateReport())
        }
    }
}