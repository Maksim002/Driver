package ru.telecor.gm.mobile.droid.model.interactors

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
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
class ReportRoutesPhotosWithTimeInteractor @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val photoInteractor: PhotoInteractor,
    private val authHolder: AuthHolder,
    private val context: Context
) {

    companion object {
        private const val ALL_ROUTE_PHOTOS_COUNT = "REPORT_ALL_ROUTE_PHOTOS_COUNT"
        private const val SENT_PHOTOS = "REPORT_SENT_PHOTOS"
        private const val SENT_PHOTOS_COUNT = "REPORT_SENT_PHOTOS_COUNT"
        private const val UNSENT_PHOTOS = "REPORT_UNSENT_PHOTOS"
        private const val UNSENT_PHOTOS_COUNT = "REPORT_UNSENT_PHOTOS_COUNT"
        private const val DAMAGED_SENT_PHOTOS = "REPORT_DAMAGED_SENT_PHOTOS"
        private const val DAMAGED_SENT_PHOTOS_COUNT = "REPORT_DAMAGED_SENT_PHOTOS_COUNT"
        private const val PHOTOS_FROM_SERVER = "REPORT_PHOTOS_FROM_SERVER"
        private const val PHOTOS_FROM_SERVER_COUNT = "REPORT_PHOTOS_FROM_SERVER_COUNT"
        private const val ROUTE_ID = "REPORT_ROUTE_ID"

        private const val DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val DRIVER_NAME = "REPORT_DRIVER_NAME"

        private const val APK_INSTALLATION_DATE = "REPORT_APK_INSTALLATION_DATE"
        private const val DATE_LAST_APK_UPDATE = "REPORT_DATE_LAST_APK_UPDATE"

        private const val REASON_UNDELIVERED_PHOTOS_OLD_VERSION = "A"
        private const val REASON_UNDELIVERED_PHOTOS_EXTERNAL_CAMERA = "B"
        private const val REASON_UNDELIVERED_PHOTOS_WAITING_RECOVERY = "C"
        private const val REASON_UNDELIVERED_PHOTOS_APK_DELETED_OR_EXTERNAL_CAMERA = "D"

        private const val NullValue = "Значение не установлено"


    }

    var TAG = javaClass.simpleName

    val allPhotosStateFlow = photoInteractor.allPhotosStateFlow

    suspend fun senReport(
        routeId: Long,
        routeInteractor: RouteInteractor
    ) {

        val driverInfo = authInteractor.driverInfo

        val driverName =
            if (driverInfo != null) driverInfo.lastName + driverInfo.firstName + driverInfo.middleName else NullValue

        val sentPhotos =
            getPhotosReport(allPhotosStateFlow.value.filter { it.routeId == routeId && it.exportStatus == ProcessingPhoto.ExportStatus.SENT })

        val unsentPhotos =
            getPhotosReport(allPhotosStateFlow.value.filter { it.routeId == routeId && it.exportStatus != ProcessingPhoto.ExportStatus.SENT })

        val damagedSentPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.SENT, routeId)

        val allRoutePhotosCount =
            photoInteractor.allPhotosStateFlow.value.filter { it.routeId == routeId }.count()

        val dateInstall =
            Date(context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime)
        val dateLastUpdate =
            Date(context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime)

        val gson = Gson()

        FirebaseCrashlytics.getInstance().run {

            setCustomKey(DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)
            setCustomKey(DRIVER_NAME, driverName)
            setCustomKey(ROUTE_ID, routeId)

            setCustomKey(APK_INSTALLATION_DATE, dateInstall.toString())
            setCustomKey(DATE_LAST_APK_UPDATE, dateLastUpdate.toString())

            setCustomKey(ALL_ROUTE_PHOTOS_COUNT, gson.toJson(allRoutePhotosCount))
            setCustomKey(DAMAGED_SENT_PHOTOS, gson.toJson(damagedSentPhotos))
            setCustomKey(DAMAGED_SENT_PHOTOS_COUNT, damagedSentPhotos.count())
            setCustomKey(SENT_PHOTOS, gson.toJson(sentPhotos))
            setCustomKey(SENT_PHOTOS_COUNT, sentPhotos.count())
            setCustomKey(UNSENT_PHOTOS, gson.toJson(unsentPhotos))
            setCustomKey(UNSENT_PHOTOS_COUNT, unsentPhotos.count())
            setCustomKey(PHOTOS_FROM_SERVER, gson.toJson(getUnsavedPhotosOnServer(routeId)))
            setCustomKey(PHOTOS_FROM_SERVER_COUNT, getUnsavedPhotosOnServer(routeId).count())

            LogUtils.error(TAG, "888888888888888888888888888888")
            recordException(ApplicationStateReport())
        }


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

    private suspend fun getUnsavedPhotosOnServer(routeId: Long): List<String> {

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
            val result = res.data.mapNotNull { it ->

                val arr = it.split(':', '_', '.')
                val rId = arr[0].toLong()
                val taskId = arr[1].toLong()
                val timestamp = arr[3]

                val photo =
                    allPhotosStateFlow.value.firstOrNull { it.routeId == rId && it.taskId == taskId && it.timestamp == timestamp }
                if (photo != null) it + ", " + if (!photo.uploadingTime.isNullOrEmpty()) getDateTime(
                    photo.uploadingTime.toLong()
                ) else "" + ", " + photo.httpCode ?: "" else null
            }
            result
        } else {
            listOf()
        }
    }

    private fun getPhotosReport(photos: List<ProcessingPhoto>): List<Array<Any>> = photos.map {
        arrayOf(
            File(it.photoPath).name,
            if (!it.uploadingTime.isNullOrEmpty()) getDateTime(it.uploadingTime.toLong()) else "",
            it.httpCode ?: ""
        )
    }

    private fun getDestroyedPhotosReport(
        exportStatus: ProcessingPhoto.ExportStatus,
        routeId: Long
    ): List<Array<Any>> {

        val photos =
            allPhotosStateFlow.value.filter { it.routeId == routeId && it.exportStatus == exportStatus }

        return photos.filter {
            (!File(it.photoPath).exists() || (File(
                it.photoPath
            ).length() / 1024).toString()
                .toInt() < 12)
        }.map {
            arrayOf(
                File(it.photoPath).name,
                if (!it.cachePath.isNullOrEmpty()) File(it.cachePath).length() else 0,
                getReasonDestroyedPhotos(it),
                if (!it.uploadingTime.isNullOrEmpty()) getDateTime(it.uploadingTime.toLong()) else "",
                it.httpCode ?: ""
            )
        }
    }

    private fun getDateTime(epoc: Long): String = photoInteractor.getDateTimef(epoc)

}