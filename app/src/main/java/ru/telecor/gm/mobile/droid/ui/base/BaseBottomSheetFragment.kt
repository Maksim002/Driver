package ru.telecor.gm.mobile.droid.ui.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.telecor.gm.mobile.droid.utils.LoadingAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.telecor.gm.mobile.droid.extensions.showToast
import ru.telecor.gm.mobile.droid.presentation.base.BaseView
import ru.telecor.gm.mobile.droid.utils.сomponent.drop.MyBottomSheetDialogFragment

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.base
 *
 * Base MVP fragment.
 *
 * Created by Artem Skopincev (aka sharpyx) 16.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
abstract class BaseBottomSheetFragment : MyBottomSheetDialogFragment(), BaseView {

    var lastClickTime: Long = 0

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 333
        lateinit var alertDialog: LoadingAlert
    }

    abstract val layoutRes: Int

    private var permissionsForRequest: Pair<String, Pair<() -> Unit, () -> Unit>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(layoutRes, container, false)!!

    override fun showMessage(msg: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                context?.showToast(msg)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissionsForRequest != null && !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val granted = permissionsForRequest?.second?.first
                if (granted != null) {
                    granted()
                }
            } else {
                val denied = permissionsForRequest?.second?.second
                if (denied != null) {
                    denied()
                }
            }
        }
    }

    fun withPermission(permission: String, granted: () -> Unit, denied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context!!,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            granted()
        } else {
            permissionsForRequest = Pair(permission, Pair(granted, denied))
            requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
        }
    }

    fun View.setOnClickListener(debounceTime: Long, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }
}