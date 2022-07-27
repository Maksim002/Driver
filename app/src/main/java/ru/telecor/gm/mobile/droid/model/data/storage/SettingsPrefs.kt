package ru.telecor.gm.mobile.droid.model.data.storage

import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo

interface SettingsPrefs {
    var isInternalCamera: Boolean
    var isInternalStorage: Boolean
    var photoPeriodDelete: Int
    var reloadDeep: Int
    var photoHeight: String
    var photoWidth: String
    var visibilityNext: Int
    var staffId: String
//    var isPhotoType: Boolean
    var isTimeCheck: Boolean
    var isDateCheck: Boolean
    var isLocationCheck: Boolean

    var isInstallationComplete: Boolean
    var isLastInstallation: Boolean

    var isSettingsPrefs: Boolean

    var numberRoute: Int

    var isLatestVersion: String
    var isInstallationRole: Boolean

    fun loader(localRouteInfo: LoaderInfo? = null): LoaderInfo
    fun secondLoader(localRouteInfo: LoaderInfo? = null): LoaderInfo
}