package ru.telecor.gm.mobile.droid.ui.dumping.fragment.successful

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class SuccessfulReceiptPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val router: Router
) : BasePresenter<SuccessfulReceiptView>() {

}
