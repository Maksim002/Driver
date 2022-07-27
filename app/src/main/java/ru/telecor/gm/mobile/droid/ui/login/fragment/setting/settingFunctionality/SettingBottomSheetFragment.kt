package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_setting_bottom_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.settingFunctionality.SettingBottomSheetPresenter
import ru.telecor.gm.mobile.droid.presentation.settingFunctionality.SettingBottomSheetView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.changingServer.ChangingServerBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.generalSettings.GeneralSettingsBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settinngPhoto.SettingsPhotoBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.updatingVersion.UpdatingVersionSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestart.fragment.message.MessageNextSheetFragment
import toothpick.Toothpick

class SettingBottomSheetFragment(
    private val settingsPhotoDialogFragment: SettingsPhotoBottomSheetFragment? = null,
    private val generalSettingsDialogFragment: GeneralSettingsBottomSheetFragment? = null,
    private val changingServerDialogFragment: ChangingServerBottomSheetFragment? = null,
    private val updatingVersionSheetFragment: UpdatingVersionSheetFragment? = null
) : BaseBottomSheetFragment(), SettingBottomSheetView {

    override val layoutRes = R.layout.fragment_setting_bottom_sheet

    @InjectPresenter
    lateinit var presenter: SettingBottomSheetPresenter

    @ProvidePresenter
    fun providePresenter(): SettingBottomSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(SettingBottomSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClick()

        cameraSettingsBtn.setOnClickListener{
            val bottomSheetDialogFragment = SettingsPhotoBottomSheetFragment(this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }

        generalSettingsBtn.setOnClickListener {
            val bottomSheetDialogFragment = GeneralSettingsBottomSheetFragment(this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }

        changingServerBtn.setOnClickListener {
            val bottomSheetDialogFragment = ChangingServerBottomSheetFragment(this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }

        updatingVersionBtn.setOnClickListener {
            val bottomSheetDialogFragment = UpdatingVersionSheetFragment(this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }

    private fun isClick() {
        exitBtn.setOnClickListener {
            val bottomSheetDialogFragment = MessageNextSheetFragment(object : MessageNextSheetFragment.Listener{
                override fun setOnClickListener() {
                    presenter.onLogOutConfirmed()
                }
            })
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
            this.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        settingsPhotoDialogFragment?.dismiss()
        generalSettingsDialogFragment?.dismiss()
        changingServerDialogFragment?.dismiss()
        updatingVersionSheetFragment?.dismiss()
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    override fun setCurrentVersion(version: String) {
        settingVersionTxt.text = version
    }

    override fun visibilityNext(boolean: Int) {
        exitBtn.visibility = boolean
        viewSet.visibility = boolean
    }
}