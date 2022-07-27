package ru.telecor.gm.mobile.droid

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.ui.about.AboutActivity
import ru.telecor.gm.mobile.droid.ui.dumping.DumpingFragment
import ru.telecor.gm.mobile.droid.ui.exitDialog.ExitDialogFragment
import ru.telecor.gm.mobile.droid.ui.garbageload.GarbageLoadFragment
import ru.telecor.gm.mobile.droid.ui.viewingPhoto.ViewingPhotoFragment
import ru.telecor.gm.mobile.droid.ui.login.LoginActivity
import ru.telecor.gm.mobile.droid.ui.login.fragment.activity.UpdatingActivity
import ru.telecor.gm.mobile.droid.ui.main.MainActivity
import ru.telecor.gm.mobile.droid.ui.qr.QrScannerActivity
import ru.telecor.gm.mobile.droid.ui.routehome.RouteHomeFragment
import ru.telecor.gm.mobile.droid.ui.routestands.RouteStandsFragment
import ru.telecor.gm.mobile.droid.ui.routestart.RouteStartFragment
import ru.telecor.gm.mobile.droid.ui.serverSettings.ServerSettingsActivity
import ru.telecor.gm.mobile.droid.ui.task.TaskFragment
import ru.telecor.gm.mobile.droid.ui.taskcompleted.TaskCompletedFragment
import ru.telecor.gm.mobile.droid.ui.tasktrouble.TaskTroubleFragment
import ru.telecor.gm.mobile.droid.ui.updateDialog.UpdateDialogFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid
 *
 * Screens of the app. Described for Cicerone library.
 *
 * Created by Artem Skopincev (aka sharpyx) 20.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */

object Screens {

    // region Activities
    object Login : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent? {
            return Intent(context, LoginActivity::class.java)
        }
    }

    object Main : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent? {
            return Intent(context, MainActivity::class.java)
        }
    }

    object Settings : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent? {
            return Intent(context, ServerSettingsActivity::class.java)
        }
    }

    object About : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent? {
            return Intent(context, AboutActivity::class.java)
        }
    }

    object Updating : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent? {
            return Intent(context, UpdatingActivity::class.java)
        }
    }

    object QrCodeScanner : SupportAppScreen() {

        override fun getActivityIntent(context: Context): Intent {
            return Intent(context, QrScannerActivity::class.java)
        }
    }

    // region Fragments
    object RouteHome : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return RouteHomeFragment.newInstance()
        }
    }

    object RouteStart : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return RouteStartFragment.newInstance()
        }
    }

    object Dumping : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return DumpingFragment.newInstance()
        }
    }

    object GarbageLoad : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return GarbageLoadFragment.newInstance()
        }
    }

    object RouteStands : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return RouteStandsFragment.newInstance()
        }
    }

    object TaskTrouble : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return TaskTroubleFragment.newInstance()
        }
    }

    object ExitDialog : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return ExitDialogFragment.newInstance()
        }
    }

    class PhotoViewing(var photo: ProcessingPhoto) : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return ViewingPhotoFragment.newInstance(photo)
        }
    }

    object UpdateDialog : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return UpdateDialogFragment.newInstance(BuildVersion.ALPHA.serverName)
        }
    }

    data class Task(val taskId: Int? = null) : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return if (taskId == null) {
                TaskFragment.newInstance()
            } else {
                TaskFragment.newInstance(taskId)
            }
        }
    }

    data class TaskCompleted(val taskId: Int, val listTask: List<TaskRelations>) : SupportAppScreen() {

        override fun getFragment(): Fragment? {
            return TaskCompletedFragment.newInstance(taskId, listTask)
        }
    }
    // endregion
}