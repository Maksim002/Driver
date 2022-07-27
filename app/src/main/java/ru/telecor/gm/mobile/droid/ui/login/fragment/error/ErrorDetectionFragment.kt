package ru.telecor.gm.mobile.droid.ui.login.fragment.error

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import kotlinx.android.synthetic.main.fragment_error_detection.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.updatingVersion.UpdatingVersionSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.NewBottomSheetFragment

class ErrorDetectionFragment : BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_error_detection

    companion object {

        private var errorMessage: String = ""

        fun newInstance(
            message: String,
        ) = ErrorDetectionFragment().apply {
            arguments = Bundle().apply {
                errorMessage = message
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginErrorMessage.text =
            errorMessage.ifEmpty { resources.getString(R.string.login_error_message) }
        Handler().postDelayed({ // Do something after 5s = 500ms
            dismiss()
        }, 1000)
    }

    override fun getTheme(): Int {
        return if (Build.VERSION.SDK_INT > 22) {
            R.style.AppBottomSheetDialogTheme
        } else {
            0
        }
    }
}