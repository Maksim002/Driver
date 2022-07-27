package ru.telecor.gm.mobile.droid.ui.routestands.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_confirmation_bottom_sheet.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment

class ConfirmationBottomSheetFragment(var string: String, var listener: Listener) :
    BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_confirmation_bottom_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textPolygon.text = string

        confirmationExitBtnYes.setOnClickListener {
            listener.setOnClickYes()
            dismiss()
        }

        confirmationExitBtnNo.setOnClickListener {
            listener.setOnClickNo()
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
        fun setOnClickYes()
        fun setOnClickNo()
    }
}