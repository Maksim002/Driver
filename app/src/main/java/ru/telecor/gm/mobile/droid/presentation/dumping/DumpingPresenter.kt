package ru.telecor.gm.mobile.droid.presentation.dumping

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.VisitPointType
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.model.data.server.CommandActionEnum
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.dumping
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 10.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class DumpingPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val router: Router,
    val settingsPrefs: SettingsPrefs,
) : BasePresenter<DumpingView>() {
//    private lateinit var timer: CountDownTimer

    var errorFlightTicketModel = MutableLiveData<String>()
    var isSavingTicket = MutableLiveData("false")
    var isTicketNumber = MutableLiveData(0)

    private lateinit var localCurrentTaskCache: TaskExtended

    var initToClick = MutableLiveData(true)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setLoadingState(false)
        refreshLocalData()
        getFlightTicketModel()
        updateTime()
    }

    private fun refreshLocalData() {
        launch {
            val res = routeInteractor.getCurrentTask()
            handleResult(res, { localCurrentTaskCache = it.data }, { handleError(it, rm) })
            updateUI()
        }
    }

    fun getFlightTicketModel() = launch {
        val res = routeInteractor.getFlightTicketModel()
        handleResult(res, { it ->
            it.data.forEach {
                if (it.current == true && isSavingTicket.value == "false" && isTicketNumber.value == 0) {

                    isSavingTicket.value = it.current.toString()
                    isTicketNumber.value = it.number!!

                    viewState.showListCoupons(it)
                } else if (isSavingTicket.value == "true" && it.number == isTicketNumber.value) {
                    viewState.showListCoupons(it)
                }
            }
        }, {
            handleError(it, rm)
            errorFlightTicketModel.value = it.toString()
        })
    }

    private fun updateUI() {
        launch {
            val vPName =
                if (::localCurrentTaskCache.isInitialized) localCurrentTaskCache.visitPoint?.name
                    ?: "" else ""
            viewState.setAddress(vPName)
            viewState.setExpectedVolume(calculateExpectedVolume())
        }
    }

    fun onNavigatorButtonClicked() {
        if (::localCurrentTaskCache.isInitialized) {
            localCurrentTaskCache.visitPoint?.let {
                viewState.startNavigationApplication(
                    it.latitude,
                    it.longitude
                )
            }
        }
    }

    fun onPolygonWeightEntered(string: String) {
        val weight = string.toIntOrNull()
        if (weight == null) {
            viewState.showWeightDialog()
            return
        }
        val standResult = StandResult(
            null, routeInteractor.processingArrivalTime?: Date().time, Date().time,
            Date().time, listOf(), 0, null, weight, stand = false
        )

        launch {
            val result = routeInteractor.addTaskToProcessing(
                localCurrentTaskCache,
                ProcessingStatusType(
                    "",
                    StatusType.SUCCESS
                ), listOf(standResult), null, true
            )
            handleResult(result, {
                router.replaceScreen(Screens.Task())
            }, {
                handleError(it, rm)
            })
        }
    }

    private suspend fun calculateExpectedVolume(): Double {
        val tasks = routeInteractor.getTasksForCurrentRoute()
        var sum = 0
        for (task in tasks) {
            //Считать объем только на пройденных точках, чтобы алгоритм не просматривал новые задачи
            if (task.statusType == StatusType.NEW) {
                break
            }
            //Если водитель заезжал на разгрузку, это точно значит что мусоровоз был опустошен
            if (task.visitPoint?.pointType?.name == VisitPointType.Type.Recycling ||
                task.visitPoint?.pointType?.name == VisitPointType.Type.Portal
            ) {
                sum = 0
                continue
            }
            //А ты знал что оказывается в выполненных тасках,
            //запрошенных с сервера появляются поля volume и count?
            //Вот и я не знал
            for (taskItem in task.taskItems) {
                //Во имя точности
                taskItem.volume?.let { sum += (it * 100).toInt() }
            }
        }
        //Double - зло
        return sum / 100.0
    }

    fun updateInternetStatus(connectionAvailable: Boolean) {
        viewState.showInternetConnection(connectionAvailable)
    }

    //Цеклично запрашивает вес до момента получения
    fun restartWeights() {
        try {
            routeInteractor.setOnTalonListener { webMessage ->
                if (webMessage.command!!.action == CommandActionEnum.attachWeighing.toString()) {
                    getFlightTicketModel()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        try {
//            timer = object : CountDownTimer(10000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
////                    Toast.makeText(activity.applicationContext, millisUntilFinished.toString() , Toast.LENGTH_LONG).show()
////                    Toast.makeText(activity.applicationContext, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_LONG).show()
//                }
//                override fun onFinish() {
//                    getFlightTicketModel()
//                }
//            }
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
    }


    private fun polygonCancel() {
        launch {
            val result = routeInteractor.polygonCancel(settingsPrefs.numberRoute)
            handleResult(result, {
                GlobalScope.launch {
                    routeInteractor.removeTask(localCurrentTaskCache)
                }
                router.navigateTo(Screens.RouteStands)
            }, {
                handleError(it, rm)
            })
        }
    }

    private fun updateTime() {
        val dateFormat = SimpleDateFormat("HH:mm")
        val time = Calendar.getInstance().time
        viewState.showCurrentTime(dateFormat.format(time))
    }

    fun updateBatteryLevel(string: String) {
        viewState.showBatteryLevel(string)
    }

//    fun timeStart() {
//        timer.start()
//    }
//
//    fun timeStop() {
//        timer.cancel()
//    }

    fun onRouteButtonClicked() {
        polygonCancel()
    }

    fun isVisibilityNext(boolean: Int) {
        settingsPrefs.visibilityNext = boolean
    }
}
