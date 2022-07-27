package ru.telecor.gm.mobile.droid.presentation.changingServer

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ChangingServerBottomSheetPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    private val settingsPrefs: SettingsPrefs
) : BasePresenter<ChangingServerBottomSheetView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
    }

    fun onConfirmPasswordClicked(password: String?) {

        when {
            password.isNullOrEmpty() || password != BuildConfig.PASSWORD -> {
                viewState.showConfirmPasswordDialog()
            }
            password == BuildConfig.PASSWORD -> {
                viewState.openSettingsScreen()
            }
        }
    }
}
