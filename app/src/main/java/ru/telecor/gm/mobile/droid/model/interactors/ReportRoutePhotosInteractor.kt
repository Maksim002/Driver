package ru.telecor.gm.mobile.droid.model.interactors

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.model.system.LocationProvider
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
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
class ReportRoutePhotosInteractor @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val photoInteractor: PhotoInteractor,
    private val authHolder: AuthHolder,
    private val directoryManager: DataStorageManager,
    private val locationProvider: LocationProvider,
    private val context: Context
) {

    companion object {
        private const val REPORT_SENT_PHOTOS = "REPORT_SENT_PHOTOS"
        private const val REPORT_SENT_PHOTOS_COUNT = "REPORT_SENT_PHOTOS_COUNT"
        private const val REPORT_READY_PHOTOS = "REPORT_RETRY_PHOTOS"
        private const val REPORT_READY_PHOTOS_COUNT = "REPORT_RETRY_PHOTOS_COUNT"
        private const val REPORT_TEMP_PHOTOS = "REPORT_TEMP_PHOTOS"
        private const val REPORT_TEMP_PHOTOS_COUNT = "REPORT_TEMP_PHOTOS_COUNT"
        private const val REPORT_PHOTOS_FROM_SERVER = "REPORT_PHOTOS_FROM_SERVER"
        private const val REPORT_ROUTE_ID = "REPORT_ROUTE_ID"

        private const val REPORT_ALL_ROUTE_PHOTOS_COUNT = "REPORT_ALL_ROUTE_PHOTOS_COUNT"
        private const val REPORT_ALL_SENT_PHOTOS = "REPORT_ALL_SENT_PHOTOS"
        private const val REPORT_ALL_SENT_PHOTOS_COUNT = "REPORT_ALL_SENT_PHOTOS_COUNT"
        private const val REPORT_ALL_RETRY_PHOTOS = "REPORT_ALL_RETRY_PHOTOS"
        private const val REPORT_ALL_RETRY_PHOTOS_COUNT = "REPORT_ALL_RETRY_PHOTOS_COUNT"
        private const val REPORT_ALL_TEMP_PHOTOS = "REPORT_ALL_TEMP_PHOTOS"
        private const val REPORT_ALL_TEMP_PHOTOS_COUNT = "REPORT_ALL_TEMP_PHOTOS_COUNT"


        private const val REPORT_DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val REPORT_DRIVER_NAME = "REPORT_DRIVER_NAME"
        private const val REPORT_FILES_IN_ACTUAL_DIRECTORY =
            "REPORT_FILES_IN_ACTUAL_DIRECTORY"
        private const val REPORT_FILES_IN_TEMP_DIRECTORY =
            "REPORT_FILES_IN_TEMP_DIRECTORY"

        private const val REPORT_CURRENT_LOCATION = "REPORT_CURRENT_LOCATION"
        private const val REPORT_CURRENT_INTERNET_SPEED = "REPORT_CURRENT_INTERNET_SPEED"
        private const val REPORT_ALLOWED_INTERNET_SPEED = "REPORT_ALLOWED_INTERNET_SPEED"
        private const val REPORT_APK_INSTALLATION_DATE = "REPORT_APK_INSTALLATION_DATE"
        private const val REPORT_DATE_LAST_APK_UPDATE = "REPORT_DATE_LAST_APK_UPDATE"
        private const val REPORT_INTERNET_INFO = "REPORT_INTERNET_INFO"


        private const val ACTUAL_TASK_PHOTOS_COUNT = "ACTUAL_TASK_PHOTOS_COUNT"

        private const val NullValue = "Значение не установлено"

        private const val REASON_UNDELIVERED_PHOTOS_OLD_VERSION = "A"
        private const val REASON_UNDELIVERED_PHOTOS_EXTERNAL_CAMERA = "B"
        private const val REASON_UNDELIVERED_PHOTOS_WAITING_RECOVERY = "C"
        private const val REASON_UNDELIVERED_PHOTOS_APK_DELETED_OR_EXTERNAL_CAMERA = "D"

        private const val REASON_PHOTO_IN_ACTUAL_DUPLICATE = "A"
        private const val REASON_PHOTO_IN_ACTUAL_INCORRECTLY_SENT_STATUS = "B"
        private const val REASON_PHOTO_IN_ACTUAL_INCORRECTLY_STATUS = "C"
        private const val REASON_PHOTO_IN_ACTUAL_IN_QUEUE = "D"
        private const val REASON_PHOTO_IN_ACTUAL_MISSING_METADATA = "E"

    }

    var TAG = javaClass.simpleName

    val allPhotosStateFlow = photoInteractor.allPhotosStateFlow

    suspend fun sendApplicationReasonUndeliveredPhotos(
        routeId: Long,
        routeInteractor: RouteInteractor
    ) {

        val loc = locationProvider.getCurrentLocation()

        loc

        val driver = authInteractor.driverInfo

        val sentPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.SENT, routeId)

        val readyPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.READY, routeId)

        val tempPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.TEMP, routeId)

        val allSentPhotos =
            getPhotosReport(ProcessingPhoto.ExportStatus.SENT, routeId)

        val allReadyPhotos =
            getPhotosReport(ProcessingPhoto.ExportStatus.READY, routeId)

        val allTempPhotos =
            getPhotosReport(ProcessingPhoto.ExportStatus.TEMP, routeId)

        val photosCountByTask = routeInteractor.getAllTaskInDevise()?.mapNotNull {

            var photosCount: String? = null
            val task = routeInteractor.getDeliveredTask(it.id.toLong())
            if (task != null) {
                if (routeId == task.routeId) {
                    val tPC = if (task.photosCount != null) task.photosCount else 0
                    val tAPC = getActualTaskPhotosCount(it.id.toLong())
                    photosCount =
                        if (tPC != tAPC) "[R:" + task.routeId + " T:" + it.id + "] M:" + tPC + "|A:" + tAPC else null
                }
            }
            photosCount
        }

        val defaultInternetInfo: String = "Не определено"

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

        val allRoutePhotosCount =
            photoInteractor.allPhotosStateFlow.value.filter { it.routeId == routeId }.count()


        val dateInstall = Date(context.packageManager.getPackageInfo( context.packageName, 0 ).firstInstallTime)
        val dateLastUpdate = Date(context.packageManager.getPackageInfo( context.packageName, 0 ).lastUpdateTime)


        val gson = Gson()

        FirebaseCrashlytics.getInstance().run {
            setCustomKey(REPORT_DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)
            setCustomKey(REPORT_SENT_PHOTOS, gson.toJson(sentPhotos))
            setCustomKey(REPORT_ALL_ROUTE_PHOTOS_COUNT,allRoutePhotosCount)
            setCustomKey(REPORT_ALL_SENT_PHOTOS, gson.toJson(allSentPhotos))
            setCustomKey(REPORT_ALL_SENT_PHOTOS_COUNT, allSentPhotos.count())
            setCustomKey(REPORT_ALL_RETRY_PHOTOS, gson.toJson(allReadyPhotos))
            setCustomKey(REPORT_ALL_RETRY_PHOTOS_COUNT, allReadyPhotos.count())
            setCustomKey(REPORT_ALL_TEMP_PHOTOS, gson.toJson(allTempPhotos))
            setCustomKey(REPORT_ALL_TEMP_PHOTOS_COUNT, allTempPhotos.count())
            setCustomKey(REPORT_SENT_PHOTOS_COUNT, sentPhotos.count())
            setCustomKey(REPORT_READY_PHOTOS, gson.toJson(readyPhotos))
            setCustomKey(REPORT_READY_PHOTOS_COUNT, readyPhotos.count())
            setCustomKey(REPORT_TEMP_PHOTOS, gson.toJson(tempPhotos))
            setCustomKey(REPORT_TEMP_PHOTOS_COUNT, tempPhotos.count())
            setCustomKey(REPORT_ROUTE_ID, routeId)
            setCustomKey(
                REPORT_PHOTOS_FROM_SERVER,
                gson.toJson(reportUnloadedPhotosReceivedFromServer(routeId))
            )
            setCustomKey(ACTUAL_TASK_PHOTOS_COUNT, gson.toJson(photosCountByTask))
            setCustomKey(
                REPORT_FILES_IN_ACTUAL_DIRECTORY,
                gson.toJson(getStatusOfFilesByDirectory("actual"))
            )
            setCustomKey(
                REPORT_FILES_IN_TEMP_DIRECTORY,
                gson.toJson(getStatusOfFilesByDirectory("taskTemp"))
            )

            val driverName = if (driver != null) {
                driver.lastName + driver.firstName + driver.middleName
            } else {
                NullValue
            }
            LogUtils.error(TAG, "888888888888888888888888888888")
            setCustomKey(REPORT_DRIVER_NAME, driverName)

            setCustomKey(
                REPORT_CURRENT_LOCATION,
                gson.toJson(arrayOf(loc?.latitude ?: 0.0, loc?.longitude ?: 0.0))
            )
            setCustomKey(REPORT_CURRENT_INTERNET_SPEED, gson.toJson(internetSpeed))
            setCustomKey(REPORT_ALLOWED_INTERNET_SPEED, gson.toJson(allowedInternetSpeed))
            setCustomKey(REPORT_APK_INSTALLATION_DATE,dateInstall.toString())
            setCustomKey(REPORT_DATE_LAST_APK_UPDATE, dateLastUpdate.toString())
            setCustomKey(REPORT_INTERNET_INFO, gson.toJson(internetConnectInfo))

            recordException(ApplicationStateReport())
        }


    }

    private fun getActualTaskPhotosCount(taskId: Long): Int {
        return allPhotosStateFlow.value.filter { it.taskId == taskId }
            .map { it.taskId }.count()
    }

    private fun getReasonDestroyedPhotos(photo: ProcessingPhoto): String {

        return if (photo.cachePath.isNullOrEmpty()) {
            REASON_UNDELIVERED_PHOTOS_OLD_VERSION
        } else {
            if (File(photo.cachePath).exists()) {
                if ((File(photo.cachePath).length() / 1024).toString().toInt() < 12) {
                    REASON_UNDELIVERED_PHOTOS_EXTERNAL_CAMERA
                } else {
                    REASON_UNDELIVERED_PHOTOS_WAITING_RECOVERY
                }
            } else {
                REASON_UNDELIVERED_PHOTOS_APK_DELETED_OR_EXTERNAL_CAMERA
            }
        }
    }

    private fun getStatusOfFilesByDirectory(photosDirectory: String): MutableList<String> {
        val resultList = mutableListOf<String>()
        File("${directoryManager.getMainDirectory()}${File.separator}${photosDirectory}").walkTopDown()
            .forEach {

                val itPhoto =
                    allPhotosStateFlow.value.firstOrNull { photo -> File(photo.photoPath).name == it.name }

                val reason = if (itPhoto != null) {
                    if (itPhoto.exportStatus == ProcessingPhoto.ExportStatus.SENT) {
                        if (File("${directoryManager.getMainDirectory()}${File.separator}archive${File.separator}${it.name}").exists()) {
                            REASON_PHOTO_IN_ACTUAL_DUPLICATE
                        } else {
                            REASON_PHOTO_IN_ACTUAL_INCORRECTLY_SENT_STATUS
                        }
                    } else {
                        if (photosDirectory == "archive") REASON_PHOTO_IN_ACTUAL_INCORRECTLY_STATUS else REASON_PHOTO_IN_ACTUAL_IN_QUEUE
                    }
                } else {
                    REASON_PHOTO_IN_ACTUAL_MISSING_METADATA
                }
                resultList += it.name + "," + it.length() + "," + reason
            }
        return resultList
    }

    private suspend fun reportUnloadedPhotosReceivedFromServer(routeId: Long): List<String> {

        val photosNameList =
            allPhotosStateFlow.value.filter {
                it.routeId == routeId &&
                        it.exportStatus == ProcessingPhoto.ExportStatus.SENT && File(
                    it.photoPath
                ).exists() && (File(it.photoPath).length() / 1024).toString().toInt() > 12
            }
                .map { photo -> File(photo.photoPath).name }

        val res = photoInteractor.checkingPhotoOnServer(photosNameList)
        return if (res is Result.Success) {
            res.data
        } else {
            listOf()
        }
    }

    private fun getPhotosReport(
        status: ProcessingPhoto.ExportStatus,
        routeId: Long
    ): List<Array<Any>> {

        return allPhotosStateFlow.value.filter { it.routeId == routeId && it.exportStatus == status }
            .map {
                arrayOf(
                    File(it.photoPath).name,
                    File(it.photoPath).length(),
                    if (!it.cachePath.isNullOrEmpty()) File(it.cachePath).length() else 0
                )
            }
    }

    private fun getDestroyedPhotosReport(
        status: ProcessingPhoto.ExportStatus,
        routeId: Long
    ): List<Array<Any>> {

        val photos =
            allPhotosStateFlow.value.filter { it.routeId == routeId && it.exportStatus == status }

        return photos.filter {
            (!File(it.photoPath).exists() || (File(
                it.photoPath
            ).length() / 1024).toString()
                .toInt() < 12)
        }.map {
            arrayOf(
                File(it.photoPath).name,
                File(it.photoPath).length(),
                if (!it.cachePath.isNullOrEmpty()) File(it.cachePath).length() else 0,
                getReasonDestroyedPhotos(it)
            )
        }
    }
}