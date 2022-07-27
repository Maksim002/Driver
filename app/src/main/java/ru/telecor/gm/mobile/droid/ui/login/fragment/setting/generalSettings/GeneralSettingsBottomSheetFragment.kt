package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.generalSettings

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_settings_bottom_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.generalSettings.GeneralSettingsBottomSheetPresenter
import ru.telecor.gm.mobile.droid.presentation.generalSettings.GeneralSettingsBottomSheetView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import toothpick.Toothpick

class GeneralSettingsBottomSheetFragment(private val bottomSheetDialogFragment: SettingBottomSheetFragment? = null) :
    BaseBottomSheetFragment(), GeneralSettingsBottomSheetView {

    override val layoutRes = R.layout.fragment_general_settings_bottom_sheet

    @InjectPresenter
    lateinit var presenter: GeneralSettingsBottomSheetPresenter

    @ProvidePresenter
    fun providePresenter(): GeneralSettingsBottomSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(GeneralSettingsBottomSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoLoaderTxt.setOnCheckedChangeListener { _, isChecked ->
            presenter.onPhotoLoader(isChecked)
        }

        generalSettingBtn.setOnClickListener {
            val bottomSheetDialogFragment = SettingBottomSheetFragment(null,this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }

    override fun onResume() {
        super.onResume()
        photoLoaderTxt.isChecked = presenter.settingsPrefs.isSettingsPrefs
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
        generalSettingVersionTxt.text = version
    }
}