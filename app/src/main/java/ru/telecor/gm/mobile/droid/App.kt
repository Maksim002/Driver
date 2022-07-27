package ru.telecor.gm.mobile.droid

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.di.modules.AppModule
import ru.telecor.gm.mobile.droid.di.modules.ServerModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid
 *
 * Application class.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initToothpick()
        initAppScope()

        FirebaseCrashlytics.getInstance().setCustomKey(
            FIREBASE_INSTANCE_NAME_KEY,
            "${BuildConfig.SERVER_PROTOCOL}://${BuildConfig.SERVER_HOST}:${BuildConfig.SERVER_PORT}/${BuildConfig.SERVER_APPLICATION}"
        )
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            Toothpick.setConfiguration(Configuration.forProduction())
        }
    }

    private fun initAppScope() {
        val appScope = Toothpick.openScope(Scopes.APP_SCOPE)
        appScope.installModules(AppModule(this))

        val serverScope = Toothpick.openScopes(Scopes.APP_SCOPE, Scopes.SERVER_SCOPE)
        serverScope.installModules(ServerModule())
    }
}