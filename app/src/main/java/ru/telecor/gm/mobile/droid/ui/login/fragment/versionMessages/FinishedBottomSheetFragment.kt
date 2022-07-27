package ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_finished_bottom_sheet.*
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R

class FinishedBottomSheetFragment(var listener: ListenerFinished, var int: Int) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (int == 0) {
            buttonEntrance.text = "Ок"
        }

        versionTxt.text = BuildConfig.VERSION_NAME

        buttonEntrance.setOnClickListener {
            listener.setOnClickListenerFinish()
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

    interface ListenerFinished{
        fun setOnClickListenerFinish()
    }
}