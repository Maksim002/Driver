package ru.telecor.gm.mobile.droid.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.db.dao.CommonDataDao
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApiLongRequest
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.telecor.gm.mobile.droid.utils.LogUtils
import ru.telecor.gm.mobile.droid.utils.dataTimePhoto
import ru.terrakok.cicerone.Router
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.repository
 *
 * Repository with common data of current Regional Operator.
 *
 * Created by Artem Skopincev (aka sharpyx) 28.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class CommonDataRepository @Inject constructor(
    private val api: TruckCrewApi,
    private val apiLongRequest: TruckCrewApiLongRequest,
    private val commonDataDao: CommonDataDao,
    private val dataStorageManager: DataStorageManager,
    private val router: Router,
    private val authRepository: AuthRepository,
    private val gmServerPrefs: GmServerPrefs

) : BaseRepository() {

    private var visitPointsCache: List<VisitPoint>? = null
    private var standsCache: List<Stand>? = null
    private var taskFailureReasonsCache: List<TaskFailureReason>? = null
    private var containerFailureReasonsCache: List<ContainerFailureReason>? = null
    private var availablePortersCache: List<LoaderInfo>? = null

    suspend fun getVisitPoints(): Result<List<VisitPoint>> {
        if (visitPointsCache != null) {
            return Result.Success(visitPointsCache!!)
        }

        return safeApiCall(Dispatchers.IO) {
            val r = api.getVisitPoints()
            visitPointsCache = r
            return@safeApiCall r
        }
    }

    suspend fun getStands(routeId: Long, update: Boolean = false): Result<List<Stand>> {
        if (!update && standsCache != null) {
            return Result.Success(standsCache!!)
        }

        return safeApiCall(Dispatchers.IO) {
            val r = apiLongRequest.getRouteStands(routeId)
            standsCache = r
            return@safeApiCall r
        }
    }

    suspend fun getTaskFailureReasons(primarilyFromServer: Boolean = false): Result<List<TaskFailureReason>> {

//        if (primarilyFromServer) {
//            val r = getTaskFailureReasonsFromServer()
//            if (r is Result.Success) {
//                return r
//            }
//        }
        val result = getTaskFailureReasonsFromServer()
        if (result is Result.Success) {
            return result
        }

        val list = commonDataDao.getTaskFailureReasons()
        list?.let {
            taskFailureReasonsCache = list
            return Result.Success(it)
        }

        taskFailureReasonsCache?.let { return Result.Success(it) }

        return getTaskFailureReasonsFromServer()
    }

    private suspend fun getTaskFailureReasonsFromServer(): Result<List<TaskFailureReason>> {
        val r = safeApiCall {
            return@safeApiCall api.getTaskFailureReasons()
        }

        if (r is Result.Success) {
            taskFailureReasonsCache = r.data
            commonDataDao.setTaskFailureReasons(r.data)
        }
        return r
    }

    suspend fun getContainerFailureReasons(primarilyFromServer: Boolean = false): Result<List<ContainerFailureReason>> {

        //Попробовать получить информацию с сервера, если нам желательно ее обновить
        //(Используется при логине, когда у нас есть возможность и время на запрос)
//        if (primarilyFromServer) {
//            val r = getContainerFailureReasonsFromServer()
//            if (r is Result.Success) {
//                return r
//            }
//        }
        val result = getContainerFailureReasonsFromServer()
        if (result is Result.Success) {
            return result
        }

        //Попробовать получить инфу с кеша
        val list = commonDataDao.getContainerFailureReasons()
        list?.let {
            containerFailureReasonsCache = it
            return Result.Success(it)
        }

        containerFailureReasonsCache?.let { return Result.Success(it) }

        //Получить инфу с сервера, если с кеша не вышло, а с сервера еще не пробовали
        return getContainerFailureReasonsFromServer()
    }

    private suspend fun getContainerFailureReasonsFromServer(): Result<List<ContainerFailureReason>> {
        val r: Result<List<ContainerFailureReason>>
        r = safeApiCall {
            return@safeApiCall api.getContainerTroubleReasons()
        }

        if (r is Result.Success) {
            containerFailureReasonsCache = r.data
            commonDataDao.setContainerFailureReasons(r.data)
        }

        return r
    }

    suspend fun getAvailablePorters(includePhoto: Boolean? = null): Result<List<LoaderInfo>> {
        // TODO: 01.04.2022 Закоментировал проврку интерента
//        if (networkSpeed < 2)  return Result.Error(Exception("Недостаточная скорость интернета"))

        if (availablePortersCache != null){
            return Result.Success(availablePortersCache!!)
        }

        return safeApiCall(Dispatchers.IO) {
            val r = api.getPorters(includePhoto!!, dataTimePhoto())
            availablePortersCache = r
            return@safeApiCall r
        }
    }

    fun getContainerLevelsList(): Result<List<ContainerLoadLevel>> {
        return Result.Success(
            listOfNotNull(
                ContainerLoadLevel("Полный", 1.0, ContainerLoadLevel.Type.FULL),
                ContainerLoadLevel("Переполнен", 1.25, ContainerLoadLevel.Type.OVERFLOWING),
                ContainerLoadLevel("Неполный 0,75", 0.75, ContainerLoadLevel.Type.PARTIAL),
                ContainerLoadLevel("Половина 0,5", 0.5, ContainerLoadLevel.Type.HALF),
                ContainerLoadLevel("Четверть 0,25", 0.25, ContainerLoadLevel.Type.FOURTH),
                ContainerLoadLevel(
                    "Пустой 0,0",
                    0.0,
                    ContainerLoadLevel.Type.EMPTY
                ).takeIf { authRepository.tourInfo?.showEmptyContainer == true },
                ContainerLoadLevel("Невозможно вывезти", 0.0, ContainerLoadLevel.Type.FAILURE)
            )
        )
    }

    fun clearAllCache() {
        // clear cache
        availablePortersCache = null
        containerFailureReasonsCache = null
        taskFailureReasonsCache = null
        visitPointsCache = null

        // clear dao
        commonDataDao.setContainerFailureReasons(listOf())
        commonDataDao.setTaskFailureReasons(listOf())
        commonDataDao.setLoadersList(listOf())
        commonDataDao.setVisitPointsList(listOf())
    }

    suspend fun getLatestVersionInfo(version: BuildVersion) =
        safeApiCall { api.getLatestVersionInfo(version.serverName) }

    fun checkForUpdates(available: () -> Unit, unavailable: () -> Unit, syncAval: Boolean = true) {
        GlobalScope.launch {
            val cVersion: BuildVersion = BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)
            val res =
                if (syncAval) getLatestVersionInfo(cVersion)
                else Result.Error(Exception("Низкая скорость интернета"))
            if (res is Result.Success) {
//                if (res.data.biggerThan(BuildConfig.VERSION_NAME) && !BuildConfig.DEBUG) {
                if (res.data.biggerThan(BuildConfig.VERSION_NAME) && cVersion != BuildVersion.BETA) {
                    available()
                } else {
                    launch {
                        unavailable()
                    }
                }
            } else {
                unavailable()
            }
        }
    }

    private fun showUpdateDialog() {
        router.navigateTo(Screens.UpdateDialog)
    }

    suspend fun downloadLatestVersion(version: String) = safeApiCall {
        val newVersionDir = File(dataStorageManager.getNewVersionDirectory())
        if (!newVersionDir.exists()) {
            newVersionDir.mkdir()
        }
        val res = api.latestVersion(version)
        val path = dataStorageManager.getNewVersionDirectory() + File.separator + "newVersion.apk"
        saveFile(res, path)
        return@safeApiCall path
    }

    private fun saveFile(body: ResponseBody?, apkFilePath: String): String {
        if (body == null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream(apkFilePath)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return apkFilePath
        } catch (e: Exception) {
            LogUtils.error("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return ""
    }
}
