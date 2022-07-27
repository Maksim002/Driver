package ru.telecor.gm.mobile.droid.ui.routestart.fragment.report

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class ReportProblemBottomPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    private val settingsPrefs: SettingsPrefs,
    private val authInteractor: AuthInteractor,
    private val routeInteractor: RouteInteractor,
    private val router: Router
) : BasePresenter<ReportProblemView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }
}
