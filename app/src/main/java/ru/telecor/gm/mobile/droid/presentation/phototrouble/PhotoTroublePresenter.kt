package ru.telecor.gm.mobile.droid.presentation.phototrouble

import android.location.Location
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.DataStorageManager
import ru.telecor.gm.mobile.droid.utils.dateTime
import java.io.File
import javax.inject.Inject

@InjectViewState
class PhotoTroublePresenter @Inject constructor(
    private val rm: IResourceManager,
    private val photoInteractor: PhotoInteractor,
    private val settingsPrefs: SettingsPrefs,
    private val dataStorageManager: DataStorageManager
) : BasePresenter<PhotoTroubleView>() {

    //Set from intent
    var routeId: Long = -1
    var taskId: Long = -1

    //Created in process of work
    var photoType: PhotoType = PhotoType.LOAD_TROUBLE
    var photoLocation: Location? = null
    var photoFile: File? = null
    var lastDonePhoto: ProcessingPhoto? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        launch {
            photoInteractor.getTaskPhotosFlow(routeId, taskId, PhotoType.LOAD_TROUBLE).collect {
                viewState.showListOfPhotos(it)
            }
        }
        launch {
            photoInteractor.getTaskPhotosFlow(routeId, taskId, PhotoType.LOAD_TROUBLE_BLOCKAGE)
                .collect {
                    viewState.showListOfBlockagePhotos(it)
                }
        }
    }

    fun getStatusPhoto(boolean: Boolean){
        photoInteractor.getStatusPhoto(boolean)
    }

    fun onConfirmButtonClicked() {
        lastDonePhoto?.let {
            viewState.setResultAndFinish(it.photoPath)
        } ?: viewState.cancel()
    }

    fun onCancelButtonClicked() {
        viewState.cancel()
    }

    fun onPhotoDone() {
        launch {
            try {
                photoFile?.let {

                    if( it.exists() && (it.length() / 1024).toString().toInt() > 12)
                    {
                        lastDonePhoto =
                            photoInteractor.addPhoto(
                                routeId,
                                taskId,
                                photoType,
                                photoLocation?.latitude ?: 0.0,
                                photoLocation?.longitude ?: 0.0,
                                it
                            )
                        if (lastDonePhoto == null) viewState.showMessage("Ошибка при создании фотографии")
                        //Добавл откат
                        viewState.back()
                    }else {
                        viewState.showMessage("Ошибка при создании фотографии")
                    }

                } ?: viewState.showMessage("Ошибка при создании фотографии")
            }catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun onPhotoDeleteClicked(photo: ProcessingPhoto) {
        launch {
            photoInteractor.deletePhoto(photo)
        }
    }

    fun onAddProblemButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_TROUBLE
        openCamera(location)
    }

    fun onAddBlockageButtonClicked(location: Location? = null) {
        photoType = PhotoType.LOAD_TROUBLE_BLOCKAGE
        openCamera(location)
    }

    //тут открывается камера
    private fun openCamera(location: Location? = null) {
        photoLocation = location
        photoFile = File.createTempFile("temp", ".jpg", dataStorageManager.getCacheDirectory())
        viewState.startExternalCameraForResult(photoFile?.absolutePath ?: return)
    }
}
