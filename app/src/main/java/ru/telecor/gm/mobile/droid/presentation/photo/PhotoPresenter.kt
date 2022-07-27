package ru.telecor.gm.mobile.droid.presentation.photo

import android.location.Location
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.R
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

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.photo
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 29.07.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
@InjectViewState
class PhotoPresenter @Inject constructor(
    private val rm: IResourceManager,
    private val photoInteractor: PhotoInteractor,
    private val settingsPrefs: SettingsPrefs,
    private val storageManager: DataStorageManager
) : BasePresenter<PhotoView>() {

    val TAG = javaClass.simpleName

    //Set from intent
    lateinit var photoType: PhotoType
    var routeId: Long = -1
    var taskId: Long = -1

    //Created in process of work
    var photoLocation: Location? = null
    var photoFile: File? = null
    var lastDonePhoto: ProcessingPhoto? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setAppBarTitle(
            when (photoType) {
                PhotoType.LOAD_BEFORE -> rm.getString(R.string.photo_activity_load_before_title)
                PhotoType.LOAD_AFTER -> rm.getString(R.string.photo_activity_load_after_title)
                PhotoType.LOAD_TROUBLE -> rm.getString(R.string.photo_activity_load_trouble_title)
                PhotoType.TASK_TROUBLE -> rm.getString(R.string.photo_activity_task_trouble_title)
                PhotoType.LOAD_TROUBLE_BLOCKAGE -> rm.getString(R.string.photo_activity_load_trouble_blockage_title)
            }
        )

        launch {

//            photoInteractor.getTaskPhotosFlow(routeId, taskId, photoType).collect {
////                viewState.showListOfPhotos(it)
//            }
        }
    }

    fun onConfirmButtonClicked() {
        lastDonePhoto?.let {
            viewState.setResultAndFinish(it.photoPath)
        } ?: viewState.cancel()
    }

    fun onCancelButtonClicked() {
        viewState.cancel()
    }

//    fun getLocation(string: String, getLongitude: Float, getWidth: Float) = photoInteractor.getLocation(string, getLongitude, getWidth)

    fun getStatusPhoto(boolean: Boolean) = photoInteractor.getStatusPhoto(boolean)

    fun setStatusPhoto() = photoInteractor.setStatusPhoto()


    //тут открывается камера
    fun onAddButtonClicked(location: Location? = null) {
        photoFile = File.createTempFile("temp", ".jpg", storageManager.getCacheDirectory())
        photoLocation = location

        viewState.startExternalCameraForResult(photoFile?.absolutePath ?: return)
    }

    fun onPhotoDone() {
        launch {
            try {
                photoFile?.let {
                    if( it.exists() &&  (it.length() / 1024).toString().toInt() > 12)
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
                        //Добавл откат
                        viewState.back()
                    }else{
                        viewState.showMessage("Ошибка при создании фотографии")
                    }
                } ?: viewState.showMessage("Ошибка при создании фотографии")
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun onPhotoDeleteClicked(photo: ProcessingPhoto) {
        launch {
            photoInteractor.deletePhoto(photo)
        }
    }
}
