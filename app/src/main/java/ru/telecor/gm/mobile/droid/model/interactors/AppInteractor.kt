package ru.telecor.gm.mobile.droid.model.interactors

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
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
class AppInteractor @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val photoInteractor: PhotoInteractor,
    private val routeInteractor: RouteInteractor,
    private val authHolder: AuthHolder
) {

    companion object {
        private const val REPORT_SENT_PHOTOS = "REPORT_SENT_PHOTOS"
        private const val REPORT_UNDELIVERED_PHOTOS = "REPORT_UNDELIVERED_PHOTOS"

        private const val REPORT_DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val REPORT_DRIVER_NAME = "REPORT_DRIVER_NAME"

        private const val REPORT_TASK_ID = "REPORT_TASK_ID"
        private const val REPORT_TASK_ADDRESS = "REPORT_TASK_ADDRESS"
        private const val REPORT_CURRENT_TASK_LIST = "REPORT_CURRENT_TASK_LIST"

        private const val NullValue = "Значение не установлено"

    }


    fun sendApplicationStateReport() {
        val task = routeInteractor.getCurrentTaskSimply()
        val driver = authInteractor.driverInfo
        val sentPhotos =
            photoInteractor.allPhotosStateFlow.value.filter { it.exportStatus == ProcessingPhoto.ExportStatus.SENT }
                .map { File(it.photoPath).name }
        val unsentPhotos =
            photoInteractor.allPhotosStateFlow.value.filter { it.exportStatus != ProcessingPhoto.ExportStatus.SENT }
                .map { File(it.photoPath).name }
        val gson = Gson()
        FirebaseCrashlytics.getInstance().run {
            setCustomKey(REPORT_DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)
            setCustomKey(REPORT_SENT_PHOTOS, gson.toJson(sentPhotos))
            setCustomKey(REPORT_UNDELIVERED_PHOTOS, gson.toJson(unsentPhotos))

            val driverName = if (driver != null) {
                driver.lastName + driver.firstName + driver.middleName
            } else {
                NullValue
            }

            setCustomKey(REPORT_DRIVER_NAME, driverName)

            setCustomKey(REPORT_TASK_ID, task?.id ?: -1)
            setCustomKey(REPORT_TASK_ADDRESS, task?.stand?.address ?: NullValue)

            setCustomKey(
                REPORT_CURRENT_TASK_LIST,
                routeInteractor.getTasksForCurrentRouteFromCache().map { it.id }.joinToString()
            )

            recordException(ApplicationStateReport())
        }

    }
}