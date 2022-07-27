package ru.telecor.gm.mobile.droid.presentation.exitDialog

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.ReportLogoutInterator
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class ExitDialogPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val authInteractor: AuthInteractor,
    private val reportLogoutInterator: ReportLogoutInterator,
    private val router: Router
) : BasePresenter<ExitDialogView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        uploadDataAndExit()
    }

    private fun uploadDataAndExit() {
        launch {
            viewState.showFailDialog()
            viewState.setLoadingState(ExitDialogView.WorkStatus.PREPARE)
            routeInteractor.sendAllInfoToServer().collect {
                val tD = if (it.taskCount != 0) it.taskCount else 1
                val pD = if (it.photoCount != 0) it.photoCount else 1

                viewState.setLoadingState(it.workStatus)

                val taskPercentage = (it.exportedTaskCount * 100F / tD)
                val photoPercentage = (it.exportedPhotoCount * 100F / pD)

                viewState.showNumUploadPhoto(
                    photoPercentage,
                    it.exportedPhotoCount,
                    it.photoCount,
                    it.destroyPhotoCount,
                    it.loadStatus
                )
                viewState.showNumUploadRoute(taskPercentage, it.exportedTaskCount, it.taskCount)

                viewState.showEstimatedTimeOfUnloading(it.estimatedTime, it.loadStatus)

                if (it.isError) {
                    viewState.showLoadingRouteDialog()
                    viewState.showMessage(it.errorMessage ?: "error")
                    viewState.setLoadingState(ExitDialogView.WorkStatus.ERROR)
                }

                if (it.isFinished) {
                    viewState.setLoadingState(ExitDialogView.WorkStatus.FINISH)
                    viewState.showSuccessDialog()
                }
            }
        }
    }

    fun onExitButtonClicked() {
        launch {
            reportLogoutInterator.sendReport()
        }
        router.newRootScreen(Screens.Login)
        routeInteractor.closeListen()
        authInteractor.logout()
    }

    fun onTryAgainButtonClicked() {
        uploadDataAndExit()
    }
}