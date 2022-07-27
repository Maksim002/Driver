package ru.telecor.gm.mobile.droid.presentation.settingPhoto

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class SettingsPhotoBottomSheetPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    val settingsPrefs: SettingsPrefs
) : BasePresenter<SettingsPhotoBottomSheetView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
    }

//    fun onPhotoType(boolean: Boolean){
//        settingsPrefs.isPhotoType = boolean
//    }

    fun onTimeCheck(boolean: Boolean){
        settingsPrefs.isTimeCheck = boolean
    }

    fun onDateCheck(boolean: Boolean){
        settingsPrefs.isDateCheck = boolean
    }

    fun onLocationCheck(boolean: Boolean){
        settingsPrefs.isLocationCheck = boolean
    }

    fun onInternalCameraSelect(boolean: Boolean) {
        settingsPrefs.isInternalCamera = boolean
    }
}
