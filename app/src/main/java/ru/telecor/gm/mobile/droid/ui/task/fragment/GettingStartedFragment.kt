package ru.telecor.gm.mobile.droid.ui.task.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_getting_started.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment

class GettingStartedFragment(var listener: Listener, var standName: String) :
    BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_getting_started

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addressText.text = standName

        btnToRoute.setOnClickListener {
            listener.setOnClickRouteListener()
            dismiss()
        }

        btnToBeginning.setOnClickListener {
            listener.setOnClickBeginningListener()
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
        fun setOnClickBeginningListener()
        fun setOnClickRouteListener()
    }
}