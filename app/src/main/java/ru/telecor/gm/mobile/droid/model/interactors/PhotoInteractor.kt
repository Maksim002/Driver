package ru.telecor.gm.mobile.droid.model.interactors

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.repository.PhotoRepository
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.interactors
 *
 * Interactor for authorization in app.
 * Use cases:
 * - Create photo file for current task
 * - Get photo's for current task
 *
 * Created by Taliya Arsembekova (aka taliyar) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class PhotoInteractor @Inject constructor(
    private val photoRepository: PhotoRepository,
) {

    val TAG = javaClass.simpleName

    val allPhotosStateFlow = photoRepository.allPhotosFlow

    fun setStatusPhoto() = photoRepository.keyPhotoName.value

    fun getStatusPhoto(boolean: Boolean){
        photoRepository.keyPhotoName.value = boolean
    }

    fun getTaskPhotosFlow(
        routeId: Long,
        taskId: Long,
        type: PhotoType
    ): Flow<List<ProcessingPhoto>> =
        flow {
            photoRepository.allPhotosFlow.collect { list ->
                this.emit(list.filter { (it.routeId == routeId) and (it.taskId == taskId) and (it.photoType == type) })
            }
        }

    fun getContext() = photoRepository.getContext()

    fun addPhoto(
        routeId: Long,
        taskId: Long,
        photoType: PhotoType,
        latitude: Double,
        longitude: Double,
        file: File
    ) = photoRepository.addPhoto(routeId, taskId, photoType, latitude, longitude, file)

    suspend fun deletePhoto(photo: ProcessingPhoto) = photoRepository.deletePhoto(photo)

    fun setTaskPhotosExportable(routeId: Long, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            for (photo in photoRepository.allPhotosFlow.value.filter { (it.routeId == routeId) and (it.taskId == taskId) }) {
                photoRepository.setPhotoExportable(photo)
            }
        }
    }

    suspend fun uploadAllUndeliveredPhotos() {
        photoRepository.recoveryMetadata()
        photoRepository.tryMigrateNonMigratedFiles()
    }

    fun getDateTimef(epoc: Long): String{
        try {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(epoc)
        } catch (e: Exception) {
            return ""
        }
    }

    suspend fun checkingPhotoOnServer(photoFileNames: List<String>): Result<List<String>> {
        return photoRepository.getUndeliveredPhotosFromList(photoFileNames)
    }
}
