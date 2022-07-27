package ru.telecor.gm.mobile.droid.ui.garbageload.fragment.message

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_getting_started.*
import kotlinx.android.synthetic.main.fragment_route_completion.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment

class RouteCompletionFragment(var listener: ListenerCompleteRoute, var standName: String?) : BaseBottomSheetFragment() {

    override val layoutRes = R.layout.fragment_route_completion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        standName?.let {
            taskDoneAddress.text = standName
        }
        initClick()
    }

    private fun initClick() {
        btnItemCancelRoute.setOnClickListener {
            dismiss()
        }

        btnItemRoute.setOnClickListener {
            listener.setOnClickRoute()
            dismiss()
        }
    }

   override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    interface ListenerCompleteRoute{
        fun setOnClickRoute()
    }
}