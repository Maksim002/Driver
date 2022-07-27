package ru.telecor.gm.mobile.droid.ui.routestart.fragment.message

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MessageNextBottomPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    private val settingsPrefs: SettingsPrefs,
) : BasePresenter<MessageNextView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }
}
