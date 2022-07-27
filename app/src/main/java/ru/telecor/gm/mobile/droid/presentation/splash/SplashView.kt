package ru.telecor.gm.mobile.droid.presentation.splash

import moxy.MvpView

interface SplashView : MvpView {

    fun openMainScreen()
    fun openLoginScreen()

    fun setCurrentVersion(version: String)
}
