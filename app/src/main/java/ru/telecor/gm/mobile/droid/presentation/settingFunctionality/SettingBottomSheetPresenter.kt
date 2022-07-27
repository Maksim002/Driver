package ru.telecor.gm.mobile.droid.presentation.settingFunctionality

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class SettingBottomSheetPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    private val settingsPrefs: SettingsPrefs,
    private val authInteractor: AuthInteractor,
    private val routeInteractor: RouteInteractor,
    private val router: Router
) : BasePresenter<SettingBottomSheetView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
        viewState.visibilityNext(settingsPrefs.visibilityNext)
    }

    fun onLogOutConfirmed() {
        authInteractor.logout()
        routeInteractor.closeListen()
        router.newRootScreen(Screens.Login)
    }
}
