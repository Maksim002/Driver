package ru.telecor.gm.mobile.droid.ui.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_login.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.LoginRequest
import ru.telecor.gm.mobile.droid.entities.VersionData
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.login.LoginPresenter
import ru.telecor.gm.mobile.droid.presentation.login.LoginView
import ru.telecor.gm.mobile.droid.ui.about.AboutActivity
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.login.fragment.error.ErrorDetectionFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.FinishedBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.LastBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.NewBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.main.MainActivity
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.telecor.gm.mobile.droid.utils.conect
import ru.telecor.gm.mobile.droid.utils.getPositionNetwork
import timber.log.Timber
import toothpick.Toothpick
import java.util.*

class LoginActivity : BaseActivity(), LoginView {

    override val layoutResId = R.layout.activity_login

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @ProvidePresenter
    fun providePresenter(): LoginPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(LoginPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPersonnelNumber.addTextChangedListener {
            ilPersonnelNumber.error = null
            ilPersonnelNumber.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gmm_white))
        }

        getPositionNetwork(this)

        val codename = Build.HARDWARE
        val device = Build.MANUFACTURER
        val androidVersion = Build.VERSION.RELEASE

        mtTop.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_settings -> {
                    presenter.isVisibilityNext(View.INVISIBLE)
                    presenter.onSettingButtonClicked()
                    true
                }
                else -> false
            }
        }

        btnLogin.setOnClickListener {
            if (conect(this, this)) {
                withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, {
                    // if android version greater than Q or equals, READ_PHONE_STATE
                    // permission will not work
                    withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                        withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, {
                            FirebaseInstallations.getInstance().id.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val loginData =
                                        getLoginData(it.result.also { fid -> Timber.w("FID is set: $fid") }
                                            ?: "".also {
                                                showMessage("FID не установлен")
                                                Timber.e("FID is null")
                                            })
                                    presenter.loginButtonPressed(
                                        etPersonnelNumber.text.toString(),
                                        loginData, androidVersion, codename, device
                                    )
                                } else {
                                    Timber.e("Unable to get FID")
                                    showMessage("Не удалось войти")
                                }
                            }

                        }, {
                            presenter.onPermissionsDenied()
                        })
                    }, {
                        presenter.onPermissionsDenied()
                    })
                }, {
                    presenter.onPermissionsDenied()
                })
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            clLoginActivity.background = ContextCompat.getDrawable(this,  R.drawable.circle_layout_common_hor)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            clLoginActivity.background = ContextCompat.getDrawable(this,  R.drawable.circle_layout_common)
        }
    }

    override fun setPersonnelNumberFieldEmptyError() {
        ilPersonnelNumber.error = getString(R.string.login_personnel_number_empty_error)
        ilPersonnelNumber.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_to_red_color))
    }

    override fun setLoadingState(value: Boolean) {
        pbLogin.visible(value, View.INVISIBLE)
        btnLogin.isEnabled = !value
        ilPersonnelNumber.isEnabled = !value
    }

    override fun openMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun getLoginData(id: String): LoginRequest {
        var gpsTime: Long = 0
        var lat: Double = 0.0
        var lon: Double = 0.0
        val time: Long = Date().time
        var imei = ""

        // get location info
        val l = LocationUtils.getBestLocation(this)
        if (l != null) {
            gpsTime = l.time
            lon = l.longitude
            lat = l.latitude
        }

        imei = id

        val versionParts = BuildConfig.VERSION_NAME.split('.')
            .map { it.toIntOrNull() }
            .toList()

        return LoginRequest(
            gpsTime, imei, "", lat, lon, time,
            VersionData(
                (versionParts[0] ?: 1) + 2,
                versionParts[1] ?: 0,
                versionParts[2] ?: 0
            )
        )
    }

    override fun openAboutScreen() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    override fun openSettingsScreen() {
        val bottomSheetDialogFragment = SettingBottomSheetFragment()
        bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun showErrorMessage(ex: Throwable) {
        Snackbar.make(
            clLoginActivity,
            R.string.login_error_snackbar,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.login_error_snackbar_action) {
                presenter.onErrorDetailedInfoClicked(ex)
            }
            .show()
    }

    override fun showErrorMessage(message: String) {
        val bottomSheetDialogFragment = ErrorDetectionFragment.newInstance(message)
        bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun showErrorDialog(ex: Throwable) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.login_error_dialog_title)
            .setMessage(ex.stackTrace.toString())
            .setNegativeButton(R.string.login_error_dialog_negative) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(R.string.login_error_dialog_positive) { _: DialogInterface, _: Int ->
                presenter.onErrorDetailedInfoClicked(ex)
            }
    }

    override fun showConfirmPasswordDialog() {
        presenter.onConfirmPasswordClicked()
    }

    override fun showUpdateDialog(version: BuildVersion) {
        NewBottomSheetFragment.newInstance(version.serverName, null, "Обновить", true)
            .show(supportFragmentManager, "newBottomSheetFragment")
    }

    override fun setCurrentVersion(version: String) {
        loginVersionTxt.text = version
    }

    override fun setCheckingVersion(versionPhone: String, versionServer: String, boolean: Boolean,  int: Int, isInCom: Boolean) {
       if (versionPhone == versionServer && boolean && isInCom){
            val bottomSheetDialogFragment = FinishedBottomSheetFragment(object : FinishedBottomSheetFragment.ListenerFinished{
                override fun setOnClickListenerFinish() {
                    presenter.settingsPrefs.isInstallationComplete = false
                }
            },int)
            bottomSheetDialogFragment.isCancelable = true
            bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment.tag)
        }
        lastInstallation(versionPhone, int)
    }

    private fun lastInstallation(versionPhone: String, int: Int){
        if (presenter.settingsPrefs.isLastInstallation){
            val bottomSheetDialogFragment = LastBottomSheetFragment(versionPhone, int, object : LastBottomSheetFragment.ListenerLast{
                override fun setOnClickClear(frag: LastBottomSheetFragment) {
                    presenter.settingsPrefs.isLastInstallation = false
                    frag.dismiss()
                }
            })
            bottomSheetDialogFragment.isCancelable = true
            bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }
}
