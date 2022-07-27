package ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_last_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_last_bottom_sheet.buttonEntrance
import kotlinx.android.synthetic.main.fragment_last_bottom_sheet.versionTxt
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R

class LastBottomSheetFragment(var name: String, var int: Int, var Listener : ListenerLast) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (int == 0){
            buttonEntrance.text = "Ок"
        }

        versionTxt.text = BuildConfig.VERSION_NAME
        installedVersionTxt.text = "Установлена последняя версия $name"

        buttonEntrance.setOnClickListener {
            Listener.setOnClickClear(this)
        }
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    interface ListenerLast {
        fun setOnClickClear(frag: LastBottomSheetFragment)
    }

}