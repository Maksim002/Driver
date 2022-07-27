package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.serverSettings

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_changing_server_bottom_sheet.changeServerVersionTxt
import kotlinx.android.synthetic.main.fragment_changing_server_bottom_sheet.toChangeServer
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.*
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.btnSave
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.ilApplication
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.ilPort
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.ilProtocol
import kotlinx.android.synthetic.main.fragment_server_settings_bottom.ilServer
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.presentation.serverSettingsN.ServerSettingsBottomPresenter
import ru.telecor.gm.mobile.droid.presentation.serverSettingsN.ServerSettingsBottomView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.changingServer.ChangingServerBottomSheetFragment
import toothpick.Toothpick

class ServerSettingsBottomFragment(
    private val changingServerDialogFragment: ChangingServerBottomSheetFragment? = null
): BaseBottomSheetFragment(), ServerSettingsBottomView {

    override val layoutRes = R.layout.fragment_server_settings_bottom

    @InjectPresenter
    lateinit var presenter: ServerSettingsBottomPresenter

    @ProvidePresenter
    fun providePresenter(): ServerSettingsBottomPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ServerSettingsBottomPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClick()
        toChangeServer.setOnClickListener {
            val bottomSheetDialogFragment = ChangingServerBottomSheetFragment(null, this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }

    private fun isClick() {
        btnSave.setOnClickListener {
            presenter.saveButtonPressed(
                ilProtocol.text.toString(),
                ilServer.text.toString(),
                ilPort.text.toString(),
                ilApplication.text.toString()
            )
        }

        cancellationBtn.setOnClickListener {
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

    override fun onResume() {
        super.onResume()
        changingServerDialogFragment!!.dismiss()
    }

    override fun setEmptyProtocolFieldError() {
        ilProtocol.error = getString(R.string.server_settings_error_text)
    }

    override fun setEmptyServerFieldError() {
        ilServer.error = getString(R.string.server_settings_error_text)
    }

    override fun setEmptyPortFieldError() {
        ilPort.error = getString(R.string.server_settings_error_text)
    }

    override fun setEmptyApplicationFieldError() {
        ilApplication.error = getString(R.string.server_settings_error_text)
    }

    override fun showCurrentServerInfo(info: GmServerInfo) {
        ilProtocol.setText(info.protocol)
        ilServer.setText(info.host)
        ilPort.setText(info.port.toString())
        ilApplication.setText(info.application)
    }

    override fun setCurrentVersion(version: String) {
        changeServerVersionTxt.text = version
    }

    override fun setTextError(boolean: Boolean) {
        errorTxt.isVisible = boolean
        errorTxt.text = resources.getString(R.string.error_txt_setting)
    }

    override fun isDismiss() {
        dismiss()
    }
}