package ru.telecor.gm.mobile.droid.presentation.routestart

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.FIREBASE_ROUTE_NUMBER_KEY
import ru.telecor.gm.mobile.droid.FIREBASE_VEHICLE_REG_NUMBER_KEY
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.LoginRequest
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.RouteStatusType
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.server.ServerError
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.ReportOnPhotosFromServer
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils.isConnectedFast
import ru.terrakok.cicerone.Router
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.Exception
import kotlin.collections.ArrayList

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.routestart
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 17.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class RouteStartPresenter @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val routeInteractor: RouteInteractor,
    private val commonDataRepository: CommonDataRepository,
    private val rm: IResourceManager,
    private val router: Router,
    val settingsPrefs: SettingsPrefs,
    private val reportOnPhotosFromServer: ReportOnPhotosFromServer,
    private val gmServerPrefs: GmServerPrefs,
    private val context: Context?
) : BasePresenter<RouteStartView>() {

    var displayingMessage = false
    var displayingWindow = false
    var routeId = 0
    var currentId = 0

    var possibleLoadersList: ArrayList<LoaderInfo>? = null
    var possibleLoadersVal: ArrayList<LoaderInfo>? = null
    var namefir = MutableLiveData<LoaderInfo>()
    var nameSec = MutableLiveData<LoaderInfo>()

    private var DEFULT_NAME =  LoaderInfo(
        "00",
        "9999",
        "Нет",
        "грузчика",
        "",
        null
    )

    private lateinit var localRouteInfo: RouteInfo
    private var data = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        updateTime()
        driverPhoto()
        // display driver info
        authInteractor.driverInfo?.also {
            viewState.setDriver(it)
        }
        viewState.sendLoginRequestAndUpdate()
    }

    override fun attachView(view: RouteStartView?) {
        super.attachView(view)
    }

    private fun driverPhoto() {
        launch {
            val result = routeInteractor.getPhotoRequest(settingsPrefs.staffId)
            handleResult(result, {
                viewState.setImProfile(true, BitmapFactory.decodeStream(it.data.byteStream()))
            }, {
//                handleError(it, rm)
                viewState.setImProfile(false)
            })
        }
    }

    fun routeClicked(routeInfo: RouteInfo, context: Context?) {
        val context = routeInteractor.getContext()
        if (!ConnectivityUtils.syncAvailability(context) && routeInteractor.startedRoute == null) {
            viewState.showMessage(
                "В начале маршрута необходимо подключиться к более скоростному интернету!"
            )
        } else {
            val syncAval =
                ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.SECONDARY)
            commonDataRepository.checkForUpdates(
                { launch { viewState.showUpdateDialog(BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)) } },
                { launch { enterRoute(routeInfo) } }, syncAval
            )
        }
    }

    private fun enterRoute(routeInfo: RouteInfo) {
        viewState.setAlert(true)
        viewState.showMessage("Производится подготовка данных, подождите!")
        FirebaseCrashlytics.getInstance().setCustomKey(FIREBASE_ROUTE_NUMBER_KEY, routeInfo.id)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(FIREBASE_VEHICLE_REG_NUMBER_KEY, routeInfo.unit.vehicle.regNumber)
        launch {
            val res = routeInteractor.startRoute(routeInfo)
            handleResult(res, {
//                router.newRootScreen(Screens.RouteHome)
                viewState.setAlert(false)
                displayingWindow = true
                viewState.setAlertDialog(router)
            }, {
                viewState.setAlert(false)
                //Если пришла ошибка, и она связна с невалидностью маршрута - запрещаем вход
                if (it.exception is ServerError) {
                    handleError(it, rm)
                } else {
                    //Если ошибка пришла не с сервера, а по физическим причинам - проверяем кеш и пропускаем
                    if (checkIfRouteIsInCache(routeInfo)) {
//                      router.newRootScreen(Screens.RouteHome)
                        displayingWindow = true
                        viewState.setAlertDialog(router)
                    } else {
                        viewState.showMessage("Произошла ошибка, пожалуйста перезапустите приложение (не выход) либо попробуйте подключиться к более скоростному интернету!")
                        handleError(it, rm)
                    }
                }
            })
        }
    }

    private fun checkIfRouteIsInCache(routeInfo: RouteInfo): Boolean {
        val cache = routeInteractor.getCurrentRouteStart()
        return cache?.id == routeInfo.id && routeInteractor.getTaskCountDb() > 0
    }

    fun updateData(loginRequest: LoginRequest) {
        launch {
            viewState.setLoadingState(true)
            authInteractor.login(loginRequest)
            // get list of routes
            handleResult(routeInteractor.getAvailableRoutes(), {
                viewState.setRoutesList(it.data)
                settingsPrefs.numberRoute = it.data[0].id.toInt()
                it.data.forEach { res ->
                    if (res.status.name.name == "INPROGRESS" || res.status.name.name == "CONFIRMED") {
                        routeId = res.id.toInt()
                        loadLocalData(res.id)
                    }
                }
            }, {
                handleError(it, rm)
            })
            viewState.setLoadingState(false)
        }
    }

    fun checkForUpdates(available: () -> Unit, unavailable: () -> Unit) {
        viewState.setLoadingState(true)

        launch {
            val cVersion: BuildVersion =
                BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)
            val res = commonDataRepository.getLatestVersionInfo(cVersion)
            handleResult(res, {
                if (it.data.biggerThan(BuildConfig.VERSION_NAME) && cVersion != BuildVersion.BETA) {
                    available()
                    viewState.setLoadingState(false)
                } else {
                    launch {
                        unavailable()
                        viewState.setLoadingState(false)
                    }
                }
            }, {
                unavailable()
            }
            )
        }
    }

    fun isVisibilityNext(boolean: Int) {
        settingsPrefs.visibilityNext = boolean
    }

    fun updateBatteryLevel(string: String) {
        viewState.showBatteryLevel(string)
    }

    private fun updateTime() {
        val dateFormat = SimpleDateFormat("HH:mm")
        val time = Calendar.getInstance().time
        viewState.showCurrentTime(dateFormat.format(time))
    }

    fun onRouteButtonClicked() {
        launch {
            reportOnPhotosFromServer.bugReportsСollection()
        }
        router.navigateTo(Screens.Task())
    }

    private fun loadLocalData(id: Long? = null) {
        viewState.setLoadingState(true)

        //Продолжить маршрут route/{routeId}/continue
        launch {
            val routeInfo =
                routeInteractor.getStartedRouteInfo(fromServer = true, id = id ?: routeId.toLong())
            handleResult(routeInfo, {
                currentId = it.data.id.toInt()
                routeId = it.data.id.toInt()
                localRouteInfo = it.data
                viewState.setRouteInfo(it.data)
                data = true
            }, {
                handleError(it, rm)
            })

            drivePhoto()

            if (drivePhoto()?.size != 0) {
                if (!settingsPrefs.isSettingsPrefs) {
                    fillingModel(settingsPrefs.isSettingsPrefs)
                }
            }

            updateUI()
        }

        routeInteractor.getPhotoLoader { drive ->
            fillingModel(drive)
        }
    }

    private fun fillingModel(drive: Boolean, def: Boolean? = false) {
        val res: ArrayList<LoaderInfo> = arrayListOf()
        launch {
            drivePhoto()
            possibleLoadersList?.forEach {
                if (!drive) {
                    res.add(
                        LoaderInfo(
                            it.employeeId ?: "",
                            it.id ?: "",
                            it.firstName ?: "",
                            it.lastName ?: "",
                            it.middleName ?: "",
                            null
                        )
                    )
                }
            }
            if (res.size != 0) {
                possibleLoadersList = arrayListOf()
                possibleLoadersVal = arrayListOf()
                possibleLoadersList = res
                possibleLoadersVal = res
            }

            if (!data) {
                selectionMovers()
            } else {
                updateUI()
            }
        }
    }

    //Подтянгивет грусчиков с фото
    private suspend fun drivePhoto(): List<LoaderInfo>? {
        val result = routeInteractor.getPossiblePorters(true)
        handleResult(result, {
            possibleLoadersList = it.data as ArrayList<LoaderInfo>
            possibleLoadersVal = it.data
        }, {
            handleError(it, rm)
            viewState.showLackInternet(it.toString())
        })
        return possibleLoadersList
    }

    private fun updateUI() {

        if (!isConnectedFast(context!!)) {
            launch {
                try {
                    if (localRouteInfo.unit.loader != null || localRouteInfo.unit.secondLoader != null) {
                        localRouteInfo.unit.loader?.let { viewState.setFirstLoaderPreselected(it) }
                        localRouteInfo.unit.secondLoader?.let {
                            viewState.setSecondLoaderPreselected(
                                it
                            )
                        }
                        displayingMessage = true
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            launch {
                possibleLoadersList?.let { viewState.setFirstLoaderList(it) }
                try {
                    localRouteInfo.unit.loader?.let {
                        val mod = possibleLoadersList?.firstOrNull {
                            it.firstName + it.lastName == localRouteInfo.unit.loader?.firstName + localRouteInfo.unit.loader?.lastName
                        }
                        if (mod != null) {
                            displayingMessage = true
                            viewState.setFirstLoaderPreselected(mod)
                            namefir.value = mod
                            setSortingListFirst()
                        } else {
                            setSortingListFirst()
                        }
                    }
                    if (localRouteInfo.unit.loader == null) {
                        namefir.value = null
                        viewState.setFirstLoaderPreselected(removingLoader())
                    }

                    localRouteInfo.unit.secondLoader?.let {
                        val mod = possibleLoadersList?.firstOrNull {
                            it.firstName + it.lastName == localRouteInfo.unit.secondLoader?.firstName + localRouteInfo.unit.secondLoader?.lastName
                        }
                        if (mod != null) {
                            displayingMessage = true
                            viewState.setSecondLoaderPreselected(mod)
                            nameSec.value = mod
                            setSortingListSecond()
                        } else {
                            setSortingListSecond()
                        }
                    }
                    if (localRouteInfo.unit.secondLoader == null) {
                        nameSec.value = null
                        viewState.setSecondLoaderPreselected(removingLoader())
                    }
                    localRouteInfo.let { viewState.setRouteInfo(it) }
                    viewState.setLoadingState(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun removingLoader(): LoaderInfo {
        return LoaderInfo("", "9999", "", "", "")
    }

    private fun setSortingListSecond() {
        try {
            possibleLoadersVal = (possibleLoadersVal?.filterNot {
                it.firstName + it.lastName == nameSec.value?.firstName + nameSec.value?.lastName
            } ?: emptyList()) as ArrayList<LoaderInfo>?
            viewState.setSecondLoaderList(possibleLoadersVal ?: arrayListOf())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSortingListFirst() {
        try {
            possibleLoadersVal = (possibleLoadersVal?.filterNot {
                it.firstName + it.lastName == namefir.value?.firstName + namefir.value?.lastName
            } ?: emptyList()) as ArrayList<LoaderInfo>?
            viewState.setFirstLoaderList(possibleLoadersVal ?: arrayListOf())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onFirstLoaderSelected(item: LoaderInfo, number: Int) {
        try {
            displayingMessage = true
            if (::localRouteInfo.isInitialized) {
                if (localRouteInfo.unit.secondLoader == item) {
                    setLoaderAndRefresh(number, null)
                }
            }
            if (item.id == "" && item.employeeId == "") {
                if (data) {
                    setLoaderAndRefresh(number, null)
                    displayingMessage = false
                } else {
                    if (number == 1) {
                        routeInteractor.namefir.value = namefir.value
                    } else if (number == 2) {
                        routeInteractor.nameSec.value = nameSec.value
                    }
                    fillingModel(settingsPrefs.isSettingsPrefs)
                }
            } else {
                //Если значение правда то запрос со статусом успешно если нет то сам видешь
                if (data) {
                    setLoaderAndRefresh(number, item)
                    displayingMessage = true
                } else {
                    if (number == 1) {
                        routeInteractor.namefir.value = namefir.value
                    } else if (number == 2) {
                        routeInteractor.nameSec.value = nameSec.value
                    }
                    fillingModel(settingsPrefs.isSettingsPrefs)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Бодроб грусчиков если для сохронения
    private fun selectionMovers() {
        val modFir = possibleLoadersList?.firstOrNull {
            it.firstName + it.lastName == namefir.value?.firstName + namefir.value?.lastName
        }
        displayingMessage = if (modFir != null) {
            viewState.setFirstLoaderPreselected(modFir)
            true
        } else {
            if (namefir.value?.firstName + namefir.value?.lastName == "Нет" + "грузчика") {
                viewState.setFirstLoaderPreselected(
                    DEFULT_NAME
                )
            }
            false
        }
        setSortingListFirst()

        val modSec = possibleLoadersList?.firstOrNull {
            it.firstName + it.lastName == nameSec.value?.firstName + nameSec.value?.lastName
        }
        displayingMessage = if (modSec != null) {
            viewState.setSecondLoaderPreselected(modSec)
            true
        }else {
            if (nameSec.value?.firstName + nameSec.value?.lastName == "Нет" + "грузчика") {
                viewState.setSecondLoaderPreselected(
                    DEFULT_NAME
                )
            }
            false
        }
        setSortingListSecond()

    }

    private fun setLoaderAndRefresh(num: Int, loaderInfo: LoaderInfo?) {
        viewState.setLoadingState(true)
        launch {
            val res = routeInteractor.setLoader(num, loaderInfo)
            handleResult(res, {}, { handleError(it, rm) })
            loadLocalData()
        }
    }
}
