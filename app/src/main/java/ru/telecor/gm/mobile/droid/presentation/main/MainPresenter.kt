package ru.telecor.gm.mobile.droid.presentation.main

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.AppInteractor
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.ReportLogoutInterator
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.main
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class MainPresenter @Inject constructor(
    private val settingsPrefs: SettingsPrefs,
    private val router: Router,
    private val routeInteractor: RouteInteractor,
    private val authInteractor: AuthInteractor,
    private val reportLogoutInterator: ReportLogoutInterator,
    private val rm: ResourceManager,
    private val appInteractor: AppInteractor
) : BasePresenter<MainView>() {

    var previousTasksListSize = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        updateTime()

        router.newRootScreen(Screens.RouteStart)

        routeInteractor.setOnSendingListener {
            viewState.setDataExportingState(it)
        }

        routeInteractor.addOnTasksUpdatedListener {
            if (it.size > previousTasksListSize) {
                launch {
//                    viewState.showMessage("Задания обновлены")
                }
            }
            previousTasksListSize = it.size
        }

        routeInteractor.setCarryContainersListener { list ->
            launch {
                viewState.setContainersCount(
                    list.size
                )
            }
        }
    }

    override fun attachView(view: MainView?) {
        super.attachView(view)
        routeInteractor.setOnSocketStateChangeListener { isOpen ->
            viewState.setConnectionEstablishedState(isOpen)
        }
        viewState.setContainersCount(routeInteractor.getCarryContainers()?.size ?: 0)
//        updateInternetStatus()
    }

    fun isVisibilityNext(boolean: Int){
      settingsPrefs.visibilityNext = boolean
    }

    fun updateLocationState(value: Boolean) {
        viewState.showLocationState(value)
    }

    fun updateBatteryLevel(string: String) {
        viewState.showBatteryLevel(string)
    }

    fun updateTime() {
        val dateFormat = SimpleDateFormat("HH:mm")
        val time = Calendar.getInstance().time
        viewState.showCurrentTime(dateFormat.format(time))
    }

    fun updateInternetStatus(connectionAvailable: Boolean) {
        viewState.showInternetConnection(connectionAvailable)
        if (connectionAvailable) {
            handleConnectionInfo()
        }
    }

    fun onHomeButtonClicked() {
        if (routeInteractor.startedRoute == null || routeInteractor.getCurrentTaskSimply() == null) {
            viewState.showMessage(rm.getString(R.string.error_route_not_started))
            return
        }

        router.navigateTo(Screens.RouteStart)
    }

    private fun handleConnectionInfo() {
        launch {
            routeInteractor.sendUndeliveredTasks()
            routeInteractor.onConnectionAvailable().collect {
                viewState.setDataExportingState(!it.isFinished)
            }
        }
    }

    fun onSettingsClicked() {
        viewState.showSettingsMenu()
    }

    fun onLogOutClicked() {
        viewState.showConfirmExitDialog()
    }

    fun onLogOutConfirmed() {
        launch {
            reportLogoutInterator.sendReport()
        }
        authInteractor.logout()
        routeInteractor.closeListen()
        router.newRootScreen(Screens.Login)
    }

    fun onAboutClicked() {
        router.navigateTo(Screens.About)
    }

    fun onReportBugClicked() {
        appInteractor.sendApplicationStateReport()
        viewState.showMessage("Отчет отправлен!")
    }
}