package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.changingServer

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_changing_server_bottom_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.changingServer.ChangingServerBottomSheetPresenter
import ru.telecor.gm.mobile.droid.presentation.changingServer.ChangingServerBottomSheetView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.serverSettings.ServerSettingsBottomFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import toothpick.Toothpick

class ChangingServerBottomSheetFragment(
    private val bottomSheetDialogFragment: SettingBottomSheetFragment? = null,
    private val serverSettingsDialogFragment: ServerSettingsBottomFragment? = null
) : BaseBottomSheetFragment(), ChangingServerBottomSheetView {

    override val layoutRes = R.layout.fragment_changing_server_bottom_sheet

    @InjectPresenter
    lateinit var presenter: ChangingServerBottomSheetPresenter

    @ProvidePresenter
    fun providePresenter(): ChangingServerBottomSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ChangingServerBottomSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClick()
    }

    private fun isClick() {
        toChangeServer.setOnClickListener {
            val bottomSheetDialogFragment = SettingBottomSheetFragment(null,null, this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }

        btnLogin.setOnClickListener {
            presenter.onConfirmPasswordClicked(etPassword.text.toString())
        }

        etPassword.addTextChangedListener {
            ilPassword.error = null
        }
    }

    override fun onResume() {
        super.onResume()
        serverSettingsDialogFragment?.dismiss()
        bottomSheetDialogFragment?.dismiss()
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    override fun setCurrentVersion(version: String) {
        changeServerVersionTxt.text = version
    }

    override fun showConfirmPasswordDialog() {
        if (etPassword.equals(0)){
            ilPassword.error = "Введите пароль"
        }else{
            ilPassword.error = "Неверный пароль"
        }
    }

    override fun openSettingsScreen() {
        val bottomSheetDialogFragment = ServerSettingsBottomFragment(this)
        bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
    }
}