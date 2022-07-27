package ru.telecor.gm.mobile.droid.model.interactors

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.reports.ApplicationStateReport
import ru.telecor.gm.mobile.droid.model.system.reports.LogoutStateReport
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 20.05.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ReportLogoutInterator @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val authHolder: AuthHolder,
    private val context: Context,
    private val routeInteractor: RouteInteractor
) {

    companion object {
        private const val REPORT_ROUTE_ID = "REPORT_ROUTE_ID"

        private const val REPORT_DRIVER_NUMBER = "REPORT_DRIVER_NUMBER"
        private const val REPORT_DRIVER_NAME = "REPORT_DRIVER_NAME"

        private const val REPORT_CURRENT_INTERNET_SPEED = "REPORT_CURRENT_INTERNET_SPEED"
        private const val REPORT_ALLOWED_INTERNET_SPEED = "REPORT_ALLOWED_INTERNET_SPEED"
        private const val REPORT_APK_INSTALLATION_DATE = "REPORT_APK_INSTALLATION_DATE"
        private const val REPORT_DATE_LAST_APK_UPDATE = "REPORT_DATE_LAST_APK_UPDATE"
        private const val REPORT_INTERNET_INFO = "REPORT_INTERNET_INFO"
        private const val REPORT_GENERATE_TIME = "REPORT_GENERATE_TIME"

        private const val REPORT_ALL_TASKS_COUNT = "ALL_TASKS_COUNT"
        private const val REPORT_COUNT_TASKS_BY_STATUS = "COUNT_TASKS_BY_STATUS"
        private const val REPORT_COUNT_TASKS_BY_TYPES = "COUNT_TASKS_BY_TYPES"

        private const val REPORT_ALL_RESULTS_COUNT = "ALL_RESULTS_COUNT"
        private const val REPORT_RESULTS_COUNT_BY_SEND_STATUS = "RESULTS_COUNT_BY_SEND_STATUS"
        private const val REPORT_RESULTS_BY_STATUS = "RESULTS_BY_STATUS"

        private const val SERVICE_TASKS = "SERVICE_TASKS"
        private const val COMMON_TASKS = "COMMON_TASKS"


        private const val NullValue = "Значение не установлено"
    }

    var TAG = javaClass.simpleName

    suspend fun sendReport() {
        val allTasks: List<TaskExtended> =
            routeInteractor.getAllTaskInDevise() ?: listOf()
        val allPResult: List<TaskProcessingResult> =
            routeInteractor.getTaskProcessingResults() ?: listOf()
        val gson = Gson()

        val driver = authInteractor.driverInfo

        val internetInfo: HashMap<String?, String?> =
            hashMapOf(
                "speed" to gson.toJson(ConnectivityUtils.getNetworkUpSpeed(context)),
                "allowedSpeed" to gson.toJson(ConnectivityUtils.isConnectedFast(context)),
                "connectInfo" to gson.toJson(ConnectivityUtils.getNetworkInfo(context))
            )

        val dateInstall =
            Date(context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime)
        val dateLastUpdate =
            Date(context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime)




        val driverName = if (driver != null) {
            driver.lastName + driver.firstName + driver.middleName
        } else {
            NullValue
        }

        FirebaseCrashlytics.getInstance().run {

            LogUtils.error(TAG, "8888-8888-8888-8888")

            setCustomKey(REPORT_DRIVER_NUMBER, authHolder.personnelNumber ?: NullValue)
            setCustomKey(REPORT_ROUTE_ID, routeInteractor.startedRoute?.id.toString() ?: "")
            setCustomKey(REPORT_DRIVER_NAME, driverName)
            setCustomKey(REPORT_GENERATE_TIME, Date().toString())
            LogUtils.error(javaClass.simpleName, Date().toString())

            internetInfo["speed"]?.let { setCustomKey(REPORT_CURRENT_INTERNET_SPEED, it) }
            internetInfo["allowedSpeed"]?.let { setCustomKey(REPORT_ALLOWED_INTERNET_SPEED, it) }
            internetInfo["connectInfo"]?.let { setCustomKey(REPORT_INTERNET_INFO, it) }

            setCustomKey(REPORT_APK_INSTALLATION_DATE, dateInstall.toString())
            setCustomKey(REPORT_DATE_LAST_APK_UPDATE, dateLastUpdate.toString())

            setCustomKey(REPORT_ALL_TASKS_COUNT, gson.toJson(allTasks.count()))
            setCustomKey(REPORT_COUNT_TASKS_BY_STATUS, gson.toJson(tasksCountByStatus(allTasks)))
            setCustomKey(REPORT_COUNT_TASKS_BY_TYPES, gson.toJson(tasksCountByType(allTasks)))

            setCustomKey(REPORT_ALL_RESULTS_COUNT, gson.toJson(allPResult.count()))
            setCustomKey(REPORT_RESULTS_COUNT_BY_SEND_STATUS, gson.toJson(processingResultByDeliveredStatus(allPResult)))
            setCustomKey(REPORT_RESULTS_BY_STATUS, gson.toJson(processingResultByStatusType(allPResult)))

            recordException(LogoutStateReport())
        }
    }


    private fun processingResultByDeliveredStatus(allPResult: List<TaskProcessingResult>): HashMap<String, Int> {
        val result: HashMap<String, Int> = hashMapOf()
        TaskProcessingResult.ProcessingStatus.values().map {
            result.put(
                it.toString(),
                allPResult.filter { result -> result.processingStatus == it }.count()
            )
        }
        return result
    }

    private fun tasksCountByStatus(allTasks: List<TaskExtended>): HashMap<String, Int> {
        val result: HashMap<String, Int> = hashMapOf()
        StatusType.values().map {
            result.put(it.toString(), allTasks.filter { task -> task.statusType == it }.count())
        }
        return result
    }

    private fun processingResultByStatusType(allPResult: List<TaskProcessingResult>): HashMap<String, Int> {
        val result: HashMap<String, Int> = hashMapOf()
        StatusType.values().map {
            result.put(it.toString(), allPResult.filter { task -> task.statusType.name == it }.count())
        }
        return result
    }

    // Report Tasks By Type
    private fun tasksCountByType(allTasks: List<TaskExtended>): HashMap<String, Int> = hashMapOf(
        SERVICE_TASKS to allTasks.filter { it.visitPoint != null }.count(),
        COMMON_TASKS to allTasks.filter { it.stand != null }.count()
    )


}