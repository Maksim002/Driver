package ru.telecor.gm.mobile.droid.ui.garbageload.fragment.message

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment

class MessageErrorMassActionFragment : BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_message_error_mass_action

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({ // Do something after 5s = 500ms
            dismiss()
        }, 2000)
    }

    override fun getTheme(): Int {
        return if (Build.VERSION.SDK_INT > 22) {
            R.style.AppBottomSheetDialogTheme
        } else {
            0
        }
    }
}