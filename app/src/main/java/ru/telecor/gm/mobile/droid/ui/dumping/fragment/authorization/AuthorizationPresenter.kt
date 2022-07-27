package ru.telecor.gm.mobile.droid.ui.dumping.fragment.authorization

import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val router: Router
) : BasePresenter<AuthorizationView>() {

    private lateinit var localCurrentTaskCache: TaskExtended

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshLocalData()
    }

    private fun refreshLocalData() {
        launch {
            val res = routeInteractor.getCurrentTask()
            handleResult(res, { localCurrentTaskCache = it.data }, { handleError(it, rm) })
        }
    }

    fun onPolygonWeightEntered(string: String) {
        val standResult = StandResult(
            null, routeInteractor.processingArrivalTime?: Date().time, Date().time,
            Date().time, listOf(), 0, null, string.toIntOrNull(), stand = false
        )

        launch {
            val result = routeInteractor.addTaskToProcessing(
                localCurrentTaskCache,
                ProcessingStatusType(
                    "",
                    StatusType.SUCCESS
                ), listOf(standResult), null, true
            )
            handleResult(result, {
                router.replaceScreen(Screens.Task())
                viewState.clearWindow()
            }, {
                handleError(it, rm)
            })
        }
    }
}
