package ru.telecor.gm.mobile.droid.presentation.garbageload.presenterMessage

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class SelectingExportPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val gmServerPrefs: GmServerPrefs,
    private val rm: IResourceManager,
    val settingsPrefs: SettingsPrefs,
    private val photoInteractor: PhotoInteractor
) : BasePresenter<SelectingExportBottomView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }
}
