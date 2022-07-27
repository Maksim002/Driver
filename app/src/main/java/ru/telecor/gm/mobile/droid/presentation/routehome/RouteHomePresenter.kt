package ru.telecor.gm.mobile.droid.presentation.routehome

import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.request.FinishRouteData
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.ReportOnPhotosFromServer
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.routehome
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 04.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class RouteHomePresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val router: Router,
    private val dataStorageManager: DataStorageManager,
    //private val reportOnPhotosFromServer: ReportOnPhotosFromServer
    private val reportOnPhotosFromServer: ReportOnPhotosFromServer,
    private val settingsPrefs: SettingsPrefs
) : BasePresenter<RouteHomeView>() {

    var possibleLoadersList: List<LoaderInfo>? = null

    var nameValue = MutableLiveData<LoaderInfo>()

    private lateinit var localRouteInfo: RouteInfo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadLocalData()
        driverPhoto()
    }

    private fun driverPhoto(){
        launch {
            val result = routeInteractor.getPhotoRequest(settingsPrefs.staffId)
            handleResult(result, {
                viewState.setImProfile(true, BitmapFactory.decodeStream(it.data.byteStream()))
            }, {
                handleError(it, rm)
                viewState.setImProfile(false)
            })
        }
    }

    private fun loadLocalData() {
        viewState.setLoadingState(true)

        launch {
            val routeInfo = routeInteractor.getStartedRouteInfo(true)
            handleResult(routeInfo, {
                localRouteInfo = it.data
                viewState.setRouteInfo(it.data)
            }, {
                launch {
                    val res = routeInteractor.getStartedRouteInfo(false)
                    handleResult(res, {
                        localRouteInfo = it.data
                        viewState.setRouteInfo(it.data)
                    }, {
                        handleError(it, rm)
                    })
                }
            })

            val result = routeInteractor.getPossiblePorters(settingsPrefs.isSettingsPrefs)
            handleResult(result, {
                possibleLoadersList = it.data
            }, {
                handleError(it, rm)
            })

            updateUI()
        }

    }

    private fun updateUI() {

        launch {
            possibleLoadersList?.let { viewState.setFirstLoaderList(it) }

            val firstLoader = localRouteInfo.unit.loader
            firstLoader?.let {
                if (nameValue.value != null) {
                    viewState.setSecondLoaderList(possibleLoadersList?.filterNot {
                        it.firstName + it.lastName == nameValue.value!!.firstName + nameValue.value!!.lastName
                    } ?: emptyList())
                }else{
                    viewState.setSecondLoaderList(possibleLoadersList!!)
                }
            }

            localRouteInfo.let { viewState.setRouteInfo(it) }
            viewState.setLoadingState(false)
        }
    }

    fun onFirstLoaderSelected(item: LoaderInfo) {
        if (localRouteInfo.unit.secondLoader == item) {
            setLoaderAndRefresh(2, null)
        }
        setLoaderAndRefresh(1, item)
    }

    fun onFirstLoaderCancelledSelection() {
        viewState.setLoadingState(true)
        launch {
            val res = routeInteractor.setLoader(1, null)
            handleResult(res, {}, { handleError(it, rm) })
            if (res is Result.Success) {
                val res2 = routeInteractor.setLoader(2, null)
                handleResult(res2, {}, { handleError(it, rm) })
            }
            loadLocalData()
        }
    }

    fun onSecondLoaderSelected(item: LoaderInfo) {
        setLoaderAndRefresh(2, item)
    }

    fun onSecondLoaderCancelledSelection() {
        setLoaderAndRefresh(2, null)
    }

    private fun setLoaderAndRefresh(num: Int, loaderInfo: LoaderInfo?) {
        viewState.setLoadingState(true)
        launch {
            val res = routeInteractor.setLoader(num, loaderInfo)
            handleResult(res, {}, { handleError(it, rm) })
            loadLocalData()
        }
    }

    fun onRouteButtonClicked() {
//        launch {
//            reportOnPhotosFromServer.bugReportsСollection()
//        }
        router.navigateTo(Screens.Task())
    }

    fun onRollbackButtonClicked() {
        router.navigateTo(Screens.RouteStart)
    }

    //never accessed cuz button is hidden
    fun onFinishButtonClicked(finishRouteData: FinishRouteData) {
//        launch {
//            val result = routeInteractor.finishRoute(finishRouteData)
//            handleResult(result, {
//                //TODO логика после завершения маршрута
//            }, {
//                handleError(it, rm)
//            })
//        }
    }

    fun onPermissionDenied() {
        viewState.showNeedPermissionMessage()
    }

    fun onExitButtonClicked() {
        router.newRootScreen(Screens.Login)
    }
}
