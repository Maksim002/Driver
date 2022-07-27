package ru.telecor.gm.mobile.droid.ui.dumping.fragment.unloading

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class CancellationPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val router: Router
) : BasePresenter<CancellationView>() {

    fun cancel(){
        router.replaceScreen(Screens.Task())
    }
}
