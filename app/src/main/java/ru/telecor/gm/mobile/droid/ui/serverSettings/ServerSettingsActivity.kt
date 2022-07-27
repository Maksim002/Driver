package ru.telecor.gm.mobile.droid.ui.serverSettings

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_server_settings.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.model.data.storage.AppPreferences
import ru.telecor.gm.mobile.droid.presentation.serverSettings.ServerSettingsPresenter
import ru.telecor.gm.mobile.droid.presentation.serverSettings.ServerSettingsView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import toothpick.Toothpick

class ServerSettingsActivity : BaseActivity(), ServerSettingsView {

    override val layoutResId = R.layout.activity_server_settings

    @InjectPresenter
    lateinit var presenter: ServerSettingsPresenter

    private var settingsPrefs = AppPreferences(this)

    @ProvidePresenter
    fun providePresenter(): ServerSettingsPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ServerSettingsPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnSave.setOnClickListener {
            presenter.saveButtonPressed(
                etProtocol.text.toString(),
                etServer.text.toString(),
                etPort.text.toString(),
                etApplication.text.toString()
            )
        }

        mToolbar.setNavigationOnClickListener {
            presenter.onBackButtonClicked()
        }

//        radio_group.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.rb_internal -> {
//                    presenter.onInternalCameraSelect(true)
//                }
//                R.id.rb_external -> {
//                    presenter.onInternalCameraSelect(false)
//                }
//            }
//        }

        locationCheck.isChecked = settingsPrefs.isTimeCheck
        geodataCheck.isChecked = settingsPrefs.isLocationCheck
        timeCheck.isChecked = settingsPrefs.isDateCheck

        locationCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onTimeCheck(isChecked)
        }

        geodataCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onLocationCheck(isChecked)
        }

        timeCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onDateCheck(isChecked)
        }

        presenter.onInternalCameraSelect(false)
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
        etProtocol.setText(info.protocol)
        etServer.setText(info.host)
        etPort.setText(info.port.toString())
        etApplication.setText(info.application)
    }

    override fun finishActivity() {
        onBackPressed()
    }

    override fun finishApp() {
        moveTaskToBack(true)
        finish()
    }

//    override fun setSelectedCamera(boolean: Boolean) {
//        when (boolean) {
//            true ->
//                rb_internal.isChecked = true
//            false -> {
//                rb_external.isChecked = true
//            }
//        }
//    }
}
