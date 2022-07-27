package ru.telecor.gm.mobile.droid.presentation.successfully


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason
import ru.telecor.gm.mobile.droid.entities.ContainerLoadLevel
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.db.PhotoProcessingForApi
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatus
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatusType
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.entities.task.TaskItem
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.presentation.garbageload.util.GarbageLoadScreenListState
import ru.telecor.gm.mobile.droid.presentation.garbageload.util.GarbageLoadScreenPickupState
import ru.telecor.gm.mobile.droid.presentation.garbageload.util.GarbageLoadScreenState
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.terrakok.cicerone.Router
import java.io.File
import java.util.*
import javax.inject.Inject

class SuccessfullyPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val commonDataRepository: CommonDataRepository,
    private val rm: IResourceManager,
    private val router: Router,
    private val photoInteractor: PhotoInteractor,
    private val dataStorageManager: DataStorageManager
) : BasePresenter<SuccessfullyLoadView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

    }

    override fun attachView(view: SuccessfullyLoadView?) {
        super.attachView(view)

    }
}
