package ru.telecor.gm.mobile.droid.presentation.tasktrouble.problem

import android.content.ClipData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import okhttp3.Headers
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.terrakok.cicerone.Router
import java.io.File
import java.util.*
import javax.inject.Inject
import ru.telecor.gm.mobile.droid.ui.splash.SplashActivity

import android.content.ClipData.newIntent
import android.os.Handler
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.startActivity
import okhttp3.internal.http2.Header
import okhttp3.internal.http2.Http2Reader
import ru.telecor.gm.mobile.droid.Screens


@InjectViewState
class ProblemPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val photoInteractor: PhotoInteractor,
    private val rm: ResourceManager,
    val router: Router,
    private val dataStorageManager: DataStorageManager
) : BasePresenter<ProblemView>() {

    var photoType: PhotoType = PhotoType.TASK_TROUBLE
    var photoFile: File? = null
    val list: ArrayList<GarbagePhotoModel> = arrayListOf()

    private lateinit var localTaskCache: TaskExtended
    private lateinit var localRouteCache: RouteInfo

    override fun attachView(view: ProblemView?) {
        super.attachView(view)

        fetchCurrentTask()
        fetchStartedRoute()

        //Заполнение адаптера фтоографиями
        for (modelType in 1..PhotoType.values().size) {
            launch {
                val type = PhotoType.values()[modelType - 1]
                photoInteractor.getTaskPhotosFlow(
                    localRouteCache.id, localTaskCache.id.toLong(), type
                ).collect { it ->
                    if (it.isNotEmpty()) {
                        if (type == PhotoType.TASK_TROUBLE)
                            viewState.showPhoto(
                                getSortingPhotos(
                                    it as ArrayList<ProcessingPhoto>,
                                    type
                                )
                            )
                    }
                }
            }
        }
    }

    private fun fetchCurrentTask() {
        val res = routeInteractor.getCurrentTask()
        handleResult(res, {
            localTaskCache = it.data
            initUi()
        }, {
            handleError(it, rm)
        })
    }

    private fun fetchStartedRoute() {
        launch {
            val route = routeInteractor.getStartedRouteInfo()
            handleResult(route, {
                localRouteCache = it.data
            }, { handleError(it, rm) })
        }
    }

    private fun initUi() {
        viewState.setMandatoryPhoto(importancePhotos())
    }

    private fun importancePhotos(): Boolean {
        if (::localTaskCache.isInitialized){
            val task = localTaskCache
            val photos =
                photoInteractor.allPhotosStateFlow.value.filter {it.taskId == task.id.toLong()}
            val photoCount = photos.size
            if (photoCount == 0) {
                val route = routeInteractor.getCurrentRoute()
                if (route.requireFailurePhoto) {
                    return true
                }
            }
        }
        return false
    }

    //Сортировка и добовление фото
    private fun getSortingPhotos(
        item: ArrayList<ProcessingPhoto>,
        modelType: PhotoType
    ): ArrayList<GarbagePhotoModel> {
        val getGarbagePhoto = list.firstOrNull { garbage -> garbage.type == modelType.toString() }
        if (getGarbagePhoto == null) {
            list.add(GarbagePhotoModel(modelType.toString(), item))
        } else {
            val type =
                list.indexOfFirst { type -> type.type == modelType.toString() && type.item.size != item.size }
            if (type != -1) {
                list.removeAt(type)
                list.addAll(type, listOf(GarbagePhotoModel(modelType.toString(), item)))
            }
        }
        return list
    }

    fun photoButtonClicked() {
        photoType = PhotoType.TASK_TROUBLE
        openCamera()
    }

    fun getPhoto(photo: ProcessingPhoto){
        router.navigateTo(Screens.PhotoViewing(photo))
    }

    //тут открывается камера
    private fun openCamera() {
        photoFile = File.createTempFile("temp", ".jpg", dataStorageManager.getCacheDirectory())
        viewState.startExternalCameraForResult(photoFile?.absolutePath ?: return)
    }

    fun getStatusPhoto(boolean: Boolean) {
        photoInteractor.getStatusPhoto(boolean)
    }

    //Удаление фотографии
    fun onPhotoDeleteClicked(photo: ProcessingPhoto) {
        launch {
            photoInteractor.deletePhoto(photo)
            val position = list.indexOfFirst { it.item == arrayListOf(photo) }
            if (position != -1) {
                list[position].item.removeAt(position)
              viewState.showPhoto(list)
                Handler().postDelayed({initUi()}, 100)
            }
        }
    }

}
