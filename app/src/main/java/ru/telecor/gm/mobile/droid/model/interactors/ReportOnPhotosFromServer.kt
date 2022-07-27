package ru.telecor.gm.mobile.droid.model.interactors

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 *
 *
 * Created by Ilimjan Baryktabasov (aka sharpyx) 06.10.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ReportOnPhotosFromServer @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val photoInteractor: PhotoInteractor,
    private val authHolder: AuthHolder,
) {

    companion object {

        private const val REPORTS_ALL_COUNT = "REPORTS_ALL_COUNT"

        private const val REPORTS_SENT_ALL_COUNT = "REPORTS_SENT_ALL_COUNT"
        private const val REPORTS_SENT_OK = "SENT_OK"
        private const val REPORTS_SENT_OK_COUNT = "SENT_OK_COUNT"
        private const val REPORTS_SENT_NOT_RESTORED = "REPORTS_SENT_NOT_RESTORED"
        private const val REPORTS_SENT_NOT_RESTORED_COUNT = "REPORTS_SENT_NOT_RESTORED_COUNT"
        private const val REPORTS_SENT_OLD_VERSION = "REPORTS_SENT_OLD_VERSION"
        private const val REPORTS_SENT_OLD_VERSION_COUNT = "REPORTS_SENT_OLD_VERSION_COUNT"
        private const val REPORTS_SENT_DELETED_COUNT = "REPORTS_SENT_DELETED_COUNT"
        private const val REPORTS_SENT_DELETED = "REPORTS_SENT_DELETED"
        private const val REPORTS_SENT_EXTERNAL_CAMERA = "REPORTS_SENT_EXTERNAL_CAMERA"
        private const val REPORTS_SENT_EXTERNAL_CAMERA_COUNT = "REPORTS_SENT_EXTERNAL_CAMERA_COUNT"


        private const val REPORTS_READY_COUNT = "REPORTS_READY_COUNT"
        private const val REPORTS_READY_IN_QUEUE = "REPORTS_READY_IN_QUEUE"
        private const val REPORTS_READY_IN_QUEUE_COUNT = "REPORTS_READY_IN_QUEUE_COUNT"
        private const val REPORTS_READY_IN_RECOVERY = "REPORTS_READY_IN_RECOVERY"
        private const val REPORTS_READY_IN_RECOVERY_COUNT = "REPORTS_READY_IN_RECOVERY_COUNT"
        private const val REPORTS_READY_OLD_VERSION = "REPORTS_READY_OLD_VERSION"
        private const val REPORTS_READY_OLD_VERSION_COUNT = "REPORTS_READY_OLD_VERSION_COUNT"
        private const val REPORTS_READY_DELETED = "REPORTS_READY_DELETED"
        private const val REPORTS_READY_DELETED_COUNT = "REPORTS_READY_DELETED_COUNT"
        private const val REPORTS_READY_EXTERNAL_CAMERA = "REPORTS_READY_EXTERNAL_CAMERA"
        private const val REPORTS_READY_EXTERNAL_CAMERA_COUNT =
            "REPORTS_READY_EXTERNAL_CAMERA_COUNT"

        private const val REPORTS_TEMP_COUNT = "REPORTS_SENT_COUNT"
        private const val REPORTS_TEMP_IN_QUEUE = "REPORTS_TEMP_IN_QUEUE"
        private const val REPORTS_TEMP_IN_QUEUE_COUNT = "REPORTS_TEMP_IN_QUEUE_COUNT"
        private const val REPORTS_TEMP_IN_RECOVERY = "REPORTS_TEMP_IN_RECOVERY"
        private const val REPORTS_TEMP_IN_RECOVERY_COUNT = "REPORTS_TEMP_IN_RECOVERY_COUNT"
        private const val REPORTS_TEMP_OLD_VERSION = "REPORTS_TEMP_OLD_VERSION"
        private const val REPORTS_TEMP_OLD_VERSION_COUNT = "REPORTS_TEMP_OLD_VERSION_COUNT"
        private const val REPORTS_TEMP_DELETED = "REPORTS_TEMP_DELETED"
        private const val REPORTS_TEMP_DELETED_COUNT = "REPORTS_TEMP_DELETED_COUNT"
        private const val REPORTS_TEMP_EXTERNAL_CAMERA = "REPORTS_TEMP_EXTERNAL_CAMERA"
        private const val REPORTS_TEMP_EXTERNAL_CAMERA_COUNT = "REPORTS_TEMP_EXTERNAL_CAMERA_COUNT"


        private const val REPORTS_DRIVER_NUMBER = "REPORTS_DRIVER_NUMBER"
        private const val REPORTS_DRIVER_NAME = "REPORTS_DRIVER_NAME"

        private const val NullValue = "Значение не установлено"


    }

    var TAG = javaClass.simpleName

    val allPhotosStateFlow = photoInteractor.allPhotosStateFlow

    suspend fun bugReportsСollection() {

        val photos =
            if (ConnectivityUtils.syncAvailability(photoInteractor.getContext())) getUndeliveredPhotosFromServer() else null
        val driver = authInteractor.driverInfo
        val gson = Gson()

        if (photos != null) {

            val sentPhotos = photos.filter { it.exportStatus == ProcessingPhoto.ExportStatus.SENT }
            val sentPhotosNormal = getNormalPhotos(sentPhotos)
            val sentPhotosDamage = getDamagePhotos(sentPhotos)
            val sentPhotosDamageOldVersion = getPhotosCreatedOnOldVersion(sentPhotosDamage)
            val sentPhotosDamageNewVersion = getPhotosCreatedOnNewVersion(sentPhotosDamage)
            val sentPhotosDamageInCache = getPhotosHasInCache(sentPhotosDamageNewVersion)
            val sentPhotosDamageDeleted = getPhotosHasNotInCache(sentPhotosDamageNewVersion)
            val sentPhotosDamageExternalCamera =
                getPhotosCreatedExternalCamera(sentPhotosDamageInCache)
            val sentPhotosDamagePendingRecovery = getPhotosPendingRecovery(sentPhotosDamageInCache)

            val readyPhotos =
                photos.filter { it.exportStatus == ProcessingPhoto.ExportStatus.READY }
            val readyPhotosNormal = getNormalPhotos(readyPhotos)
            val readyPhotosDamage = getDamagePhotos(readyPhotos)
            val readyPhotosDamageOldVersion = getPhotosCreatedOnOldVersion(readyPhotosDamage)
            val readyPhotosDamageNewVersion = getPhotosCreatedOnNewVersion(readyPhotosDamage)
            val readyPhotosDamageInCache = getPhotosHasInCache(readyPhotosDamageNewVersion)
            val readyPhotosDamageDeleted = getPhotosHasNotInCache(readyPhotosDamageNewVersion)
            val readyPhotosDamageExternalCamera =
                getPhotosCreatedExternalCamera(readyPhotosDamageInCache)
            val readyPhotosDamagePendingRecovery =
                getPhotosPendingRecovery(readyPhotosDamageInCache)

            val tempPhotos = photos.filter { it.exportStatus == ProcessingPhoto.ExportStatus.TEMP }
            val tempPhotosNormal = getNormalPhotos(tempPhotos)
            val tempPhotosDamage = getDamagePhotos(tempPhotos)
            val tempPhotosDamageOldVersion = getPhotosCreatedOnOldVersion(tempPhotosDamage)
            val tempPhotosDamageNewVersion = getPhotosCreatedOnNewVersion(tempPhotosDamage)
            val tempPhotosDamageInCache = getPhotosHasInCache(tempPhotosDamageNewVersion)
            val tempPhotosDamageDeleted = getPhotosHasNotInCache(tempPhotosDamageNewVersion)
            val tempPhotosDamageExternalCamera =
                getPhotosCreatedExternalCamera(tempPhotosDamageInCache)
            val tempPhotosDamagePendingRecovery = getPhotosPendingRecovery(tempPhotosDamageInCache)

            FirebaseCrashlytics.getInstance().run {
                setCustomKey(REPORTS_DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)

                setCustomKey(REPORTS_ALL_COUNT, photos.count())

                setCustomKey(REPORTS_SENT_ALL_COUNT, sentPhotos.count())
                setCustomKey(REPORTS_SENT_OK, gson.toJson(getPhotosInfo(sentPhotosNormal)))
                setCustomKey(REPORTS_SENT_OK_COUNT, sentPhotosNormal.count())
                setCustomKey(
                    REPORTS_SENT_NOT_RESTORED,
                    gson.toJson(getPhotosInfo(sentPhotosDamagePendingRecovery))
                )
                setCustomKey(
                    REPORTS_SENT_NOT_RESTORED_COUNT,
                    sentPhotosDamagePendingRecovery.count()
                )
                setCustomKey(
                    REPORTS_SENT_OLD_VERSION,
                    gson.toJson(getPhotosInfo(sentPhotosDamageOldVersion))
                )
                setCustomKey(REPORTS_SENT_OLD_VERSION_COUNT, sentPhotosDamageOldVersion.count())
                setCustomKey(
                    REPORTS_SENT_DELETED,
                    gson.toJson(getPhotosInfo(sentPhotosDamageDeleted))
                )
                setCustomKey(REPORTS_SENT_DELETED_COUNT, sentPhotosDamageDeleted.count())
                setCustomKey(
                    REPORTS_SENT_EXTERNAL_CAMERA,
                    gson.toJson(getPhotosInfo(sentPhotosDamageExternalCamera))
                )
                setCustomKey(
                    REPORTS_SENT_EXTERNAL_CAMERA_COUNT,
                    sentPhotosDamageExternalCamera.count()
                )

                setCustomKey(REPORTS_READY_COUNT, readyPhotos.count())
                setCustomKey(REPORTS_READY_IN_QUEUE, gson.toJson(getPhotosInfo(readyPhotosNormal)))
                setCustomKey(REPORTS_READY_IN_QUEUE_COUNT, readyPhotosNormal.count())
                setCustomKey(
                    REPORTS_READY_IN_RECOVERY,
                    gson.toJson(getPhotosInfo(readyPhotosDamagePendingRecovery))
                )
                setCustomKey(
                    REPORTS_READY_IN_RECOVERY_COUNT,
                    readyPhotosDamagePendingRecovery.count()
                )
                setCustomKey(
                    REPORTS_READY_OLD_VERSION,
                    gson.toJson(getPhotosInfo(readyPhotosDamageOldVersion))
                )
                setCustomKey(REPORTS_READY_OLD_VERSION_COUNT, readyPhotosDamageOldVersion.count())
                setCustomKey(
                    REPORTS_READY_DELETED,
                    gson.toJson(getPhotosInfo(readyPhotosDamageDeleted))
                )
                setCustomKey(REPORTS_READY_DELETED_COUNT, readyPhotosDamageDeleted.count())
                setCustomKey(
                    REPORTS_READY_EXTERNAL_CAMERA,
                    gson.toJson(getPhotosInfo(readyPhotosDamageExternalCamera))
                )
                setCustomKey(
                    REPORTS_READY_EXTERNAL_CAMERA_COUNT,
                    readyPhotosDamageExternalCamera.count()
                )

                setCustomKey(REPORTS_TEMP_COUNT, tempPhotos.count())
                setCustomKey(REPORTS_TEMP_IN_QUEUE, gson.toJson(getPhotosInfo(tempPhotosNormal)))
                setCustomKey(REPORTS_TEMP_IN_QUEUE_COUNT, tempPhotosNormal.count())
                setCustomKey(
                    REPORTS_TEMP_IN_RECOVERY,
                    gson.toJson(getPhotosInfo(tempPhotosDamagePendingRecovery))
                )
                setCustomKey(
                    REPORTS_TEMP_IN_RECOVERY_COUNT,
                    tempPhotosDamagePendingRecovery.count()
                )
                setCustomKey(
                    REPORTS_TEMP_OLD_VERSION,
                    gson.toJson(getPhotosInfo(tempPhotosDamageOldVersion))
                )
                setCustomKey(REPORTS_TEMP_OLD_VERSION_COUNT, tempPhotosDamageOldVersion.count())
                setCustomKey(
                    REPORTS_TEMP_DELETED,
                    gson.toJson(getPhotosInfo(tempPhotosDamageDeleted))
                )
                setCustomKey(REPORTS_TEMP_DELETED_COUNT, tempPhotosDamageDeleted.count())
                setCustomKey(
                    REPORTS_TEMP_EXTERNAL_CAMERA,
                    gson.toJson(getPhotosInfo(tempPhotosDamageExternalCamera))
                )
                setCustomKey(
                    REPORTS_TEMP_EXTERNAL_CAMERA_COUNT,
                    tempPhotosDamageExternalCamera.count()
                )


                val driverName = if (driver != null) {
                    driver.lastName + driver.firstName + driver.middleName
                } else {
                    NullValue
                }

                setCustomKey(REPORTS_DRIVER_NAME, driverName)

                LogUtils.error(TAG, "888888888888888888888")
                recordException(ApplicationStateReport())
            }

        }
    }

    private fun getNormalPhotos(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter {
            File(it.photoPath).exists() && (File(it.photoPath).length() / 1024).toString()
                .toInt() > 0
        }


    private fun getDamagePhotos(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter {
            !File(it.photoPath).exists() || (File(it.photoPath).length() / 1024).toString()
                .toInt() <= 0
        }


    private fun getPhotosCreatedOnOldVersion(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { it.cachePath.isNullOrEmpty() }


    private fun getPhotosCreatedOnNewVersion(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { !it.cachePath.isNullOrEmpty() }


    private fun getPhotosHasInCache(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { File(it.cachePath).exists() }

    private fun getPhotosHasNotInCache(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { !File(it.cachePath).exists() }

    private fun getPhotosCreatedExternalCamera(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { (File(it.photoPath).length() / 1024).toString().toInt() <= 0 }


    private fun getPhotosPendingRecovery(photos: List<ProcessingPhoto>): List<ProcessingPhoto> =
        photos.filter { (File(it.photoPath).length() / 1024).toString().toInt() > 0 }


    private suspend fun getUndeliveredPhotosFromServer(): List<ProcessingPhoto>? {

        val photosNameList =
            allPhotosStateFlow.value.map { photo -> File(photo.photoPath).name }

        val res = photoInteractor.checkingPhotoOnServer(photosNameList)

        return if (res is Result.Success) {
            getPhotosByName(res.data)
        } else {
            null
        }
    }

    private fun getPhotosByName(list: List<String>): List<ProcessingPhoto> =
        list.mapNotNull {
            val arr = it.split(':', '_', '.')
            val routeId = arr[0].toLong()
            val taskId = arr[1].toLong()
            val timestamp = arr[3]
            val photos =
                allPhotosStateFlow.value.firstOrNull { it.routeId == routeId && it.taskId == taskId && it.timestamp == timestamp }
            photos
        }

    private fun getPhotosInfo(photos: List<ProcessingPhoto>): List<Array<Any>> =
        photos.map {
            arrayOf(
                try {
                    File(it.photoPath).name
                } catch (e: Exception) {
                    "${it.routeId}${File.pathSeparator}${it.taskId}${File.pathSeparator}${it.photoType}_${it.timestamp}.jpg"
                },
                File(it.photoPath).length(),
                if (!it.cachePath.isNullOrEmpty()) File(it.cachePath).length() else 0
            )
        }
}
