package ru.telecor.gm.mobile.droid.model.interactors

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
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
class ReportRouteAllPhotosInteractor @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val photoInteractor: PhotoInteractor,
    private val authHolder: AuthHolder,
    private val directoryManager: DataStorageManager,
) {
    companion object {
        private const val REPORT_SENT_PHOTOS = "REPORT_SENT_PHOTOS"
        private const val REPORT_SENT_PHOTOS_COUNT = "REPORT_SENT_PHOTOS_COUNT"
        private const val REPORT_READY_PHOTOS = "REPORT_RETRY_PHOTOS"
        private const val REPORT_READY_PHOTOS_COUNT = "REPORT_RETRY_PHOTOS_COUNT"
        private const val REPORT_TEMP_PHOTOS = "REPORT_TEMP_PHOTOS"
        private const val REPORT_TEMP_PHOTOS_COUNT = "REPORT_TEMP_PHOTOS_COUNT"
        private const val REPORT_PHOTOS_FROM_SERVER = "REPORT_PHOTOS_FROM_SERVER"

        private const val REPORT_DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val REPORT_DRIVER_NAME = "REPORT_DRIVER_NAME"
        private const val REPORT_FILES_IN_ACTUAL_DIRECTORY =
            "REPORT_FILES_IN_ACTUAL_DIRECTORY"
        private const val REPORT_FILES_IN_TEMP_DIRECTORY =
            "REPORT_FILES_IN_TEMP_DIRECTORY"

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

    suspend fun sendApplicationReasonUndeliveredPhotos(routeInteractor: RouteInteractor) {

        val driver = authInteractor.driverInfo
        val sentPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.SENT)


        val readyPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.READY)

        val tempPhotos =
            getDestroyedPhotosReport(ProcessingPhoto.ExportStatus.TEMP)

        val photosCountByTask = routeInteractor.getAllTaskInDevise()?.mapNotNull {
            val task = routeInteractor.getDeliveredTask(it.id.toLong())
            val tPC = if (task?.photosCount != null) task.photosCount else 0
            val tAPC = getActualTaskPhotosCount(it.id.toLong())


            val photosCount = if (tPC != tAPC) {
                "[R:" + (task?.routeId ?: "U") + " T:" + it.id + "] M:" + tPC + "|A:" + tAPC
            } else {
                null
            }
            photosCount
        }

        val gson = Gson()

        FirebaseCrashlytics.getInstance().run {
            setCustomKey(REPORT_DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)
            setCustomKey(REPORT_SENT_PHOTOS, gson.toJson(sentPhotos))
            setCustomKey(REPORT_SENT_PHOTOS_COUNT, sentPhotos.count())
            setCustomKey(REPORT_READY_PHOTOS, gson.toJson(readyPhotos))
            setCustomKey(REPORT_READY_PHOTOS_COUNT, readyPhotos.count())
            setCustomKey(REPORT_TEMP_PHOTOS, gson.toJson(tempPhotos))
            setCustomKey(REPORT_TEMP_PHOTOS_COUNT, tempPhotos.count())
            setCustomKey(
                REPORT_PHOTOS_FROM_SERVER,
                gson.toJson(reportUnloadedPhotosReceivedFromServer())
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
            LogUtils.error(TAG, "888888888888888888888")
            setCustomKey(REPORT_DRIVER_NAME, driverName)

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

    private suspend fun reportUnloadedPhotosReceivedFromServer(): List<String> {

        val photosNameList =
            allPhotosStateFlow.value.filter {
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

    private fun getDestroyedPhotosReport(status: ProcessingPhoto.ExportStatus): List<Array<Any>> {

        val photos = allPhotosStateFlow.value.filter { it.exportStatus == status }

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