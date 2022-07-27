package ru.telecor.gm.mobile.droid.ui.routestart.fragment.message

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_message_next_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import toothpick.Toothpick

class MessageNextSheetFragment(var listener: Listener) : BaseBottomSheetFragment(),
    MessageNextView {

    override val layoutRes = R.layout.fragment_message_next_sheet

    @InjectPresenter
    lateinit var presenter: MessageNextBottomPresenter

    @ProvidePresenter
    fun providePresenter(): MessageNextBottomPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(MessageNextBottomPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        returnBtn.setOnClickListener {
            val bottomSheetDialogFragment = SettingBottomSheetFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
            this.dismiss()
        }

        sureBtn.setOnClickListener {
            listener.setOnClickListener()
        }
    }

    override fun getTheme(): Int {
        return if(Build.VERSION.SDK_INT > 22){
            R.style.AppBottomSheetDialogTheme
        }else{
            0
        }
    }
    interface Listener{
        fun setOnClickListener()
    }

}