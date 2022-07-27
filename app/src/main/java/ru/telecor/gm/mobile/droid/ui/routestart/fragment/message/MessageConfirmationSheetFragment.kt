package ru.telecor.gm.mobile.droid.ui.routestart.fragment.message

import android.os.Bundle
import android.view.View
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import android.os.Build
import kotlinx.android.synthetic.main.fragment_message_confirmation_sheet.*

class MessageConfirmationSheetFragment(var listener: Listener) : BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_message_confirmation_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sureBtn.setOnClickListener {
            listener.setOnClickListener()
            dismiss()
        }

        returnBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return if (Build.VERSION.SDK_INT > 22) {
            R.style.AppBottomSheetDialogTheme
        } else {
            0
        }
    }

    interface Listener {
        fun setOnClickListener()
    }

}