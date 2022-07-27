package ru.telecor.gm.mobile.droid.model.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.model.data.db.dao.CommonDataDao
import ru.telecor.gm.mobile.droid.model.data.db.dao.TourDao
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.storage
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class LocalDataPrefs @Inject constructor(
    private val context: Context,
    private val gson: Gson
): TourDao, CommonDataDao {

    companion object {

        private const val localStorageKey = "storage-local"

        private const val keyTourInfo = "tourInfo"
        private const val keyVisitPoints = "visitPoints"
        private const val keyLoaders = "loaders"
        private const val keyCurrentRoute = "currentRoute"
        private const val keyContainerFailureReasons = "containerFailures"
        private const val keyTaskFailureReasons = "taskFailures"
        private const val keyCarryContainers = "carryContainers"
        private const val keyCarryContainersHistory = "carryContainersHistory"

        private const val keyPhotos = "processingPhotos"
    }

    private inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    private val prefs: SharedPreferences = context.getSharedPreferences(
        localStorageKey,
        Context.MODE_PRIVATE
    )


    // endregion


    override fun getTourInfo(): TourInfo? {
        val json = prefs.getString(keyTourInfo, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setTourInfo(tourInfo: TourInfo?) {
        val json = gson.toJson(tourInfo)

        prefs.edit().putString(keyTourInfo, json).apply()
    }

    override fun setCurrentRoute(routeInfo: RouteInfo?) {
        val json = gson.toJson(routeInfo)

        prefs.edit().putString(keyCurrentRoute, json).apply()
    }

    override fun getCurrentRoute(): RouteInfo? {
        val json = prefs.getString(keyCurrentRoute, null) ?: return null

        return gson.fromJson(json)
    }

    override fun getLoaders(): List<LoaderInfo>? {
        val json = prefs.getString(keyLoaders, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setLoadersList(list: List<LoaderInfo>) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyLoaders, json).apply()
    }

    override fun getVisitPoints(): List<VisitPoint>? {
        val json = prefs.getString(keyVisitPoints, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setVisitPointsList(list: List<VisitPoint>) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyVisitPoints, json).apply()
    }

    override fun setContainerFailureReasons(list: List<ContainerFailureReason>) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyContainerFailureReasons, json).apply()
    }

    override fun getContainerFailureReasons(): List<ContainerFailureReason>? {
        val json = prefs.getString(keyContainerFailureReasons, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setTaskFailureReasons(list: List<TaskFailureReason>) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyTaskFailureReasons, json).apply()
    }

    override fun getTaskFailureReasons(): List<TaskFailureReason>? {
        val json = prefs.getString(keyTaskFailureReasons, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setCarryContainers(list: List<CarryContainer>?) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyCarryContainers, json).apply()
    }

    override fun getCarryContainers(): List<CarryContainer>? {
        val json = prefs.getString(keyCarryContainers, null) ?: return null

        return gson.fromJson(json)
    }

    override fun setCarryContainersHistory(list: List<CarryContainerHistoryItem>?) {
        val json = gson.toJson(list)

        prefs.edit().putString(keyCarryContainersHistory, json).apply()
    }

    override fun getCarryContainersHistory(): List<CarryContainerHistoryItem>? {
        val json = prefs.getString(keyCarryContainersHistory, null) ?: return null

        return gson.fromJson(json)
    }
}