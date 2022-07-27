package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settinngPhoto

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings_photo_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_settings_photo_bottom_sheet.geoDataCheck
import kotlinx.android.synthetic.main.fragment_settings_photo_bottom_sheet.locationCheck
import kotlinx.android.synthetic.main.fragment_settings_photo_bottom_sheet.timeCheck
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.settingPhoto.SettingsPhotoBottomSheetPresenter
import ru.telecor.gm.mobile.droid.presentation.settingPhoto.SettingsPhotoBottomSheetView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import toothpick.Toothpick

class SettingsPhotoBottomSheetFragment(private val bottomSheetDialogFragment: SettingBottomSheetFragment? = null) :
    BaseBottomSheetFragment(), SettingsPhotoBottomSheetView {

    override val layoutRes = R.layout.fragment_settings_photo_bottom_sheet

    @InjectPresenter
    lateinit var presenter: SettingsPhotoBottomSheetPresenter

    @ProvidePresenter
    fun providePresenter(): SettingsPhotoBottomSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(SettingsPhotoBottomSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        photoTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
//            presenter.onPhotoType(isChecked)
//        }

        locationCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onTimeCheck(isChecked)
        }

        geoDataCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onLocationCheck(isChecked)
        }

        timeCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.onDateCheck(isChecked)
        }

        presenter.onInternalCameraSelect(false)

        idReturnBtn.setOnClickListener {
            val bottomSheetDialogFragment = SettingBottomSheetFragment(this)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }

    override fun onResume() {
        super.onResume()
//        photoTypeSwitch.isChecked = presenter.settingsPrefs.isPhotoType
        locationCheck.isChecked = presenter.settingsPrefs.isTimeCheck
        geoDataCheck.isChecked = presenter.settingsPrefs.isLocationCheck
        timeCheck.isChecked = presenter.settingsPrefs.isDateCheck
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
        settingPhotoVersionTxt.text = version
    }
}