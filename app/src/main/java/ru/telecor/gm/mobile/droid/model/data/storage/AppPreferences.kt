package ru.telecor.gm.mobile.droid.model.data.storage

import android.content.Context
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.model.BuildCon
import ru.telecor.gm.mobile.droid.utils.LogUtils
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.storage
 *
 * Class for access to Android application preferences.
 *
 * Created by Artem Skopincev (aka sharpyx) 03.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class AppPreferences @Inject constructor(private val context: Context) : AuthHolder, GmServerPrefs,
    SettingsPrefs {

    private val prefsName = "appPreferences"
    private val keyPersonnelNumber = "keyPersonnelNumber"

    companion object {
        private const val keyTourInfo = "tourInfo"
        private const val keyTasks = "tasks"
        private const val keyVisitPoints = "visitPoints"
        private const val keyLoaders = "loaders"
        private const val keyBuildVersion = "buildVersion"
        private const val keyServerInfo = "serverInfo"
        private const val keySettingsCamera = "settingsCamera"
        private const val keySettingsStorage = "settingsStorage"

        private const val keyPhotoPeriodDelete = "photoPeriodDelete"
        private const val keyReloadDeep = "reloadDeep"
        private const val keyPhotoHeight = "photoHeight"
        private const val keyPhotoWidth = "photoWidth"

        private const val keyInstallationComplete = "installationComplete"
        private const val keyLastInstallation = "lastInstallation"

//        private const val keyPhotoType = "keyPhotoType"
        private const val keyTimeCheck = "isTimeCheck"
        private const val keyDateCheck = "isDateCheck"
        private const val keyLocationCheck = "isLocationCheck"
        private const val keyVisibilityNext = "isVisibilityNext"
        private const val keyNumberRoute = "isNumberRoute"
        private const val keyStaffId = "isStaffId"
        private const val keyLatestVersionId = "isLatestVersionId"
        private const val keyInstallationRole = "isInstallationRole"

        private const val keySettingsPrefs = "isSettingsPrefs"
    }

    override var personnelNumber: String?
        get() = getPrefs().getString(keyPersonnelNumber, null)
        set(value) {
            getPrefs().edit().putString(keyPersonnelNumber, value).apply()
        }

    private fun getPrefs() = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override fun getGmServerInfo(): GmServerInfo? {
        val json = getPrefs().getString(keyServerInfo, null) ?: return null

        return Gson().fromJson(json, GmServerInfo::class.java)
    }

    override fun setGmServerInfo(info: GmServerInfo) {
        val json = Gson().toJson(info)

        getPrefs().edit().putString(keyServerInfo, json).apply()
    }

    override fun getGmBuildCon(): BuildCon {
        var json = getPrefs().getString(keyBuildVersion, null)
        if (json == null){
            setGmBuildCon(BuildCon(buildVersion = "alpha"))
            json = getPrefs().getString(keyBuildVersion, null) ?: BuildCon(buildVersion = "alpha").toString()
        }
        return Gson().fromJson(json, BuildCon::class.java)
    }

    override fun setGmBuildCon(info: BuildCon) {
        val json = Gson().toJson(info)

        getPrefs().edit().putString(keyBuildVersion, json).apply()
    }

    override var photoPeriodDelete : Int
        get() = getPrefs().getInt(keyPhotoPeriodDelete, 0)
        set(value) {
            getPrefs().edit().putInt(keyPhotoPeriodDelete, value).apply()
        }
    override var reloadDeep: Int
        get() = getPrefs().getInt(keyReloadDeep, 0)
        set(value) {
            getPrefs().edit().putInt(keyReloadDeep, value).apply()
        }

    override var photoHeight: String
        get() = getPrefs().getString(keyPhotoHeight, "")!!
        set(value) {
            getPrefs().edit().putString(keyPhotoHeight, value).apply()
        }

    override var photoWidth: String
        get() = getPrefs().getString(keyPhotoWidth, "")!!
        set(value) {
            getPrefs().edit().putString(keyPhotoWidth, value).apply()
        }
    override var visibilityNext: Int
        get() = getPrefs().getInt(keyVisibilityNext, 0)
        set(value) {
            getPrefs().edit().putInt(keyVisibilityNext, value).apply()
        }
    override var staffId: String
        get() = getPrefs().getString(keyStaffId, "")!!
        set(value) {
            getPrefs().edit().putString(keyStaffId, value).apply()
        }

//    override var isPhotoType: Boolean
//        get() = getPrefs().getBoolean(keyPhotoType, true)
//        set(value) {
//            getPrefs().edit().putBoolean(keyPhotoType, value).apply()
//        }

    override var isTimeCheck: Boolean
        get() = getPrefs().getBoolean(keyTimeCheck, true)
        set(value) {
            getPrefs().edit().putBoolean(keyTimeCheck, value).apply()
        }

    override var isDateCheck: Boolean
        get() = getPrefs().getBoolean(keyDateCheck, true)
        set(value) {
            getPrefs().edit().putBoolean(keyDateCheck, value).apply()
        }

    override var isLocationCheck: Boolean
        get() = getPrefs().getBoolean(keyLocationCheck, true)
        set(value) {
            getPrefs().edit().putBoolean(keyLocationCheck, value).apply()
        }

    override var isInstallationComplete: Boolean
        get() = getPrefs().getBoolean(keyInstallationComplete, false)
        set(value) {
            getPrefs().edit().putBoolean(keyInstallationComplete, value).apply()
        }
    override var isLastInstallation: Boolean
        get() = getPrefs().getBoolean(keyLastInstallation, false)
        set(value) {
            getPrefs().edit().putBoolean(keyLastInstallation, value).apply()
        }

    override var isSettingsPrefs: Boolean
        get() = getPrefs().getBoolean(keySettingsPrefs, true)
        set(value) {
            getPrefs().edit().putBoolean(keySettingsPrefs, value).apply()
        }

    override var numberRoute: Int
        get() = getPrefs().getInt(keyNumberRoute, 0)
        set(value) {
            getPrefs().edit().putInt(keyNumberRoute, value).apply()
        }

    override var isLatestVersion: String
        get() = getPrefs().getString(keyLatestVersionId, "")!!
        set(value) {
            getPrefs().edit().putString(keyLatestVersionId, value).apply()
        }
    override var isInstallationRole: Boolean
        get() = getPrefs().getBoolean(keyInstallationRole, true)
        set(value) {
            getPrefs().edit().putBoolean(keyInstallationRole, value).apply()
        }

    override fun loader(localRouteInfo: LoaderInfo?): LoaderInfo {
        val json = getPrefs().getString(keyServerInfo, null) ?: return null!!
        return Gson().fromJson(json, LoaderInfo::class.java)
    }

    override fun secondLoader(localRouteInfo: LoaderInfo?): LoaderInfo {
        val json = getPrefs().getString(keyServerInfo, null) ?: return null!!
        return Gson().fromJson(json, LoaderInfo::class.java)
    }

    override var isInternalCamera: Boolean
        get() = getPrefs().getBoolean(keySettingsCamera, true)
        set(value) {
            getPrefs().edit().putBoolean(keySettingsCamera, value).apply()
        }

    override var isInternalStorage: Boolean
        get() = getPrefs().getBoolean(keySettingsStorage, true)
        set(value) {
            getPrefs().edit().putBoolean(keySettingsStorage, value).apply()
        }
}