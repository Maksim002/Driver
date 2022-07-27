package ru.telecor.gm.mobile.droid.ui.dumping.fragment.сlosingMessage

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment

class ClosingMessageSheetFragment(var listener: ListenerClosingMessage) : BaseBottomSheetFragment(){

    override val layoutRes = R.layout.fragment_closing_message_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    private fun initClick() {
        Handler().postDelayed({
            listener.setonClickListener()
            Handler().postDelayed({
                dismiss()
            },2000)
        },500)
    }

    interface ListenerClosingMessage{
        fun setonClickListener()
    }
}