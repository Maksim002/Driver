package ru.telecor.gm.mobile.droid.presentation.problem

import android.location.Location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.terrakok.cicerone.Router
import java.io.File
import javax.inject.Inject

class ProblemPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val rm: IResourceManager,
    private val photoInteractor: PhotoInteractor,
    private val router: Router,
    private val dataStorageManager: DataStorageManager
) : BasePresenter<ProblemLoadView>() {

    private lateinit var localTaskCache: TaskExtended
    private lateinit var localRouteCache: RouteInfo
    private lateinit var localTaskDraftProcessingResult: TaskDraftProcessingResult
    private val list: ArrayList<GarbagePhotoModel> = arrayListOf()

    var photoFile: File? = null
    var photoType: PhotoType = PhotoType.LOAD_TROUBLE
    var photoLocation: Location? = null

    override fun attachView(view: ProblemLoadView?) {
        super.attachView(view)
        fetchCurrentTask()
        fetchStartedRoute()

        //Заполнение адаптера фтоографиями
        for (modelType in 1..PhotoType.values().size) {
            launch {
                val type = PhotoType.values()[modelType-1]
                photoInteractor.getTaskPhotosFlow(localRouteCache.id, localTaskCache.id.toLong(), type).collect { it ->
                    if (it.isNotEmpty()) {
                        if (type == PhotoType.LOAD_TROUBLE){
                            viewState.initRecyclerView(getSortingPhotos(it as ArrayList<ProcessingPhoto>, type))
                        }
                    }
                }
            }
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

    private fun fetchStartedRoute() {
        launch {
            val route = routeInteractor.getStartedRouteInfo()
            handleResult(route, { localRouteCache = it.data }, { handleError(it, rm) })
        }
    }

    //Сортировка и добовление фото
    private fun getSortingPhotos(item: ArrayList<ProcessingPhoto>, modelType: PhotoType): ArrayList<GarbagePhotoModel>{
        val getGarbagePhoto = list.firstOrNull { garbage -> garbage.type == modelType.toString()}
        if (getGarbagePhoto == null) {
            list.add(GarbagePhotoModel(modelType.toString(), item))
        }else{
            val type = list.indexOfFirst {type -> type.type == modelType.toString() && type.item.size != item.size}
            if (type != -1){
                list.removeAt(type)
                list.addAll(type, listOf(GarbagePhotoModel(modelType.toString(), item)))
            }
        }
        return list
    }

    //Удаление фотографии
    fun onPhotoDeleteClicked(photo: ProcessingPhoto) {
        launch {
            photoInteractor.deletePhoto(photo)
            val position = list.indexOfFirst {it.item == arrayListOf(photo)}
            if (position != -1){
                if (list[position].item.size == 1){
                    list[position].item.removeAt(position)
                }else{
                    list[position].item.removeAt(position)
                }
                viewState.initRecyclerView(list)
            }
        }
    }

    fun getPhoto(photo: ProcessingPhoto){
        router.navigateTo(Screens.PhotoViewing(photo))
    }

    fun getStatusPhoto(boolean: Boolean){
        photoInteractor.getStatusPhoto(boolean)
    }

    fun photoBeforeButtonClicked(location: Location? = null) {
        photoType =  PhotoType.LOAD_TROUBLE
        openCamera(location)
    }

    //тут открывается камера
    private fun openCamera(location: Location? = null) {
        photoLocation = location
        photoFile = File.createTempFile("temp", ".jpg", dataStorageManager.getCacheDirectory())
        viewState.startExternalCameraForResult(photoFile?.absolutePath ?: return)
    }
}
