package ru.telecor.gm.mobile.droid.ui.base

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpAppCompatDialogFragment
import ru.telecor.gm.mobile.droid.extensions.showToast
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

abstract class BaseDialogFragment : MvpAppCompatDialogFragment(), BaseView {

    private var permissionsForRequest: Pair<String, Pair<() -> Unit, () -> Unit>>? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BaseFragment.REQUEST_CODE_PERMISSIONS) {
            if (permissionsForRequest != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    override fun showMessage(msg: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                context?.showToast(msg)
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
            requestPermissions(arrayOf(permission), BaseFragment.REQUEST_CODE_PERMISSIONS)
        }
    }
}