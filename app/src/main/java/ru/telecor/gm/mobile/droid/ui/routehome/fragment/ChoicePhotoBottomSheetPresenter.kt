package ru.telecor.gm.mobile.droid.ui.routehome.fragment

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ChoicePhotoBottomSheetPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    val settingsPrefs: SettingsPrefs
) : BasePresenter<ChoicePhotoBottomSheetView>() {

}
