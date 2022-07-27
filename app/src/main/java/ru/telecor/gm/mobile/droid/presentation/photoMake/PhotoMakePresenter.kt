package ru.telecor.gm.mobile.droid.presentation.photoMake

import android.location.Location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.telecor.gm.mobile.droid.utils.dateTime
import java.io.File
import javax.inject.Inject

@InjectViewState
class PhotoMakePresenter @Inject constructor(
    private val rm: ResourceManager,
    private val photoInteractor: PhotoInteractor,
    private val storageManager: DataStorageManager,
    private val routeInteractor: RouteInteractor,
) : BasePresenter<PhotoMakeView>() {

    lateinit var tempFile: File
    var isFlashEnabled: Boolean = false

    //Created in process of work
    var photoType: PhotoType = PhotoType.LOAD_TROUBLE
    var photoLocation: Location? = null
    var lastDonePhoto: ProcessingPhoto? = null

    private lateinit var localRouteCache: RouteInfo
    private lateinit var localTaskCache: TaskExtended
    private lateinit var localTaskDraftProcessingResult: TaskDraftProcessingResult

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        fetchCurrentTask()
        fetchStartedRoute()

        tempFile = File.createTempFile("temp", ".jpg", storageManager.getCacheDirectory())
        viewState.setTempFile(tempFile)
    }

    override fun attachView(view: PhotoMakeView?) {
        super.attachView(view)
        viewState.setCurrentFlashState(isFlashEnabled)
    }

    private fun fetchStartedRoute() {
        launch {
            val route = routeInteractor.getStartedRouteInfo()
            handleResult(route, { localRouteCache = it.data }, { handleError(it, rm) })
        }
    }

    private fun fetchCurrentTask() {
        val res = routeInteractor.getCurrentTask()
        handleResult(res, {
            GlobalScope.launch {
                val taskDraftData = routeInteractor.getDraftByTaskID(it.data.id.toLong())
                handleResult(taskDraftData, { tdRes ->
                    localTaskDraftProcessingResult = tdRes.data
                }, {})
            }
            localTaskCache = it.data
        }, {
            handleError(it, rm)
        })
    }

    fun onFlashButtonPressed() {
        isFlashEnabled = !isFlashEnabled
        viewState.setCurrentFlashState(isFlashEnabled)
    }

    fun onTakePhotoButtonPressed() {
        viewState.takePhoto(isFlashEnabled)
    }

    fun onPhotoTaken() {
        viewState.showPreviewDialog(true)
    }

    fun onPhotoAccepted() {
        viewState.setResultAndFinish()
    }

    fun onPhotoRejected() {
        viewState.showPreviewDialog(false)
    }

    fun onPhotoDone(photoFile: File) {
        launch {
            try {
                photoFile.let {
                    if (it.exists() && (it.length() / 1024).toString().toInt() > 12) {
                        lastDonePhoto =
                            photoInteractor.addPhoto(
                                localRouteCache.id,
                                localTaskCache.id.toLong(),
                                photoType,
                                photoLocation?.latitude ?: 0.0,
                                photoLocation?.longitude ?: 0.0,
                                it,
                            )
                        if(lastDonePhoto == null) viewState.showMessage("Ошибка при создании фотографии") else viewState.back()
                    } else {
                        viewState.showMessage("Ошибка при создании фотографии")
                    }
                } ?: viewState.showMessage("Ошибка при создании фотографии")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}