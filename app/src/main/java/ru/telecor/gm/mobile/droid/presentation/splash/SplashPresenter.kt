package ru.telecor.gm.mobile.droid.presentation.splash

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import javax.inject.Inject

@InjectViewState
class SplashPresenter @Inject constructor(
    private val authInteractor: AuthInteractor
) : MvpPresenter<SplashView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)

        if (authInteractor.isLoggedIn()) {
            viewState.openMainScreen()
        } else {
            viewState.openLoginScreen()
        }
    }
}
