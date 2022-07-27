package ru.telecor.gm.mobile.droid.di.modules

import android.content.Context
import android.provider.Settings
import androidx.room.Room
import ru.telecor.gm.mobile.droid.AppConstants
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.data.db.AppDatabase
import ru.telecor.gm.mobile.droid.model.data.db.MIGRATION_6_7
import ru.telecor.gm.mobile.droid.model.data.db.dao.*
import ru.telecor.gm.mobile.droid.model.system.ILogger
import ru.telecor.gm.mobile.droid.model.data.storage.AppPreferences
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.system.*
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.modules
 *
 * Dependencies for the app.
 *
 * Created by Artem Skopincev (aka sharpyx) 16.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class AppModule(private val context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)
        bind(IResourceManager::class.java).toInstance(ResourceManager(context))

        val prefs = AppPreferences(context)

        bind(AuthHolder::class.java).toInstance(prefs)
        bind(GmServerPrefs::class.java).toInstance(prefs)
        bind(SettingsPrefs::class.java).toInstance(prefs)

        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        bind(IDirectoryManager::class.java).toInstance(DirectoryManager(context))
        bind(EDirectoryManager::class.java).toInstance(ExternalDirectoryManager(context))

        bind(ILogger::class.java).toInstance(Logger())

        bind(LocationProvider::class.java).toInstance(DeviceLocationProvider(context))

        // db
        // TODO убрать нахуй при конечной сборке fallbackToDestructiveMigration
        val database =
            Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.APP_DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_6_7)
                .build()
        bind(AppDatabase::class.java).toInstance(database)


        val processingPhotoDao: ProcessingPhotoDao = database.processingPhotoDao()
        bind(ProcessingPhotoDao::class.java).toInstance(processingPhotoDao)

        val taskExtendedDao: TaskExtendedDao = database.taskExtendedDao()
        bind(TaskExtendedDao::class.java).toInstance(taskExtendedDao)

        val taskProcessingResultDao: TaskProcessingResultDao = database.taskProcessingResultDao()
        bind(TaskProcessingResultDao::class.java).toInstance(taskProcessingResultDao)

        val taskDraftProcessingResultDao: TaskDraftProcessingResultDao =
            database.taskDraftProcessingResultDao()
        bind(TaskDraftProcessingResultDao::class.java).toInstance(taskDraftProcessingResultDao)

        val taskRelationsDaoDao: TaskRelationsDao =
            database.taskRelationsDaoDao()
        bind(TaskRelationsDao::class.java).toInstance(taskRelationsDaoDao)


        bind(AppSystemInfo::class.java).toInstance(
            AppSystemInfo(
                BuildConfig.APP_NAME,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            )
        )
    }
}