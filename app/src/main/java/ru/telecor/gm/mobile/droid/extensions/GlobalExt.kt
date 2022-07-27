package ru.telecor.gm.mobile.droid.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.extensions
 *
 * Global Kotlin extensions for anything Android-project.
 *
 * Created by Artem Skopincev (aka sharpyx) 13.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */

/**
 * Showing toast in current context
 *
 * @param message Message to show by toast
 * @param duration Short by default
 * @param otherContext If need use other context
 */
fun Context.showToast(
    message: String, duration: Int = Toast.LENGTH_SHORT,
    otherContext: Context? = null
) {
    Toast.makeText(otherContext ?: this, message, duration).show()
}

/**
 * Returns int color from resource.
 * @param resId Resource ID
 */
fun Context.color(resId: Int) = ContextCompat.getColor(this, resId)

/**
 * Choose view Visible state. By default invisible (false) is GONE.
 * @param visible true or false
 * @param invisibleState by default - GONE, may be INVISIBLE
 */
fun View.visible(visible: Boolean, invisibleState: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else invisibleState
}

/**
 * Return Date from Long (this).
 */
fun Long.toDate(): Date {
    return Date(this * 1000L)
}

inline fun <T> tryOrNull(block: () -> T?): T? {
    return try {
        block()
    } catch (e: Throwable) {
        null
    }
}