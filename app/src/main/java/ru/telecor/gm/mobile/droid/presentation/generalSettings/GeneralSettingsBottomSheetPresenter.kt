package ru.telecor.gm.mobile.droid.presentation.generalSettings

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class GeneralSettingsBottomSheetPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    val settingsPrefs: SettingsPrefs,
    private val routeInteractor: RouteInteractor
) : BasePresenter<GeneralSettingsBottomSheetView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
    }

    fun onPhotoLoader(boolean: Boolean){
        settingsPrefs.isSettingsPrefs = boolean
        routeInteractor.setPhotoLoader(settingsPrefs.isSettingsPrefs)
    }
}
