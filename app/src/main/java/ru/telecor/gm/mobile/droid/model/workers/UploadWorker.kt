package ru.telecor.gm.mobile.droid.model.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.repository.PhotoRepository
import toothpick.Toothpick
import javax.inject.Inject

class UploadWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var photoRepository: PhotoRepository

    override suspend fun doWork(): Result {
        val appScope = Toothpick.openScope(Scopes.SERVER_SCOPE)
        Toothpick.inject(this, appScope)

        val availability = photoRepository.getAvailabilityToUpload()
        if (availability) {


            val photo = ProcessingPhoto.fromJson(inputData.getString("PHOTO_INPUT") ?: "")
            val res = photoRepository.uploadPhoto(photo)
            val taskTag = String.format("%s_%d_%d", photo.timestamp, photo.routeId, photo.taskId)

            if (res is ru.telecor.gm.mobile.droid.model.data.server.Result.Success) {
                WorkManager.getInstance(applicationContext).cancelAllWorkByTag(taskTag)
                return Result.success()
            } else {
                if (!photoRepository.allPhotosFlow.value.filter { it.taskId == photo.taskId && it.routeId == photo.routeId && it.timestamp == photo.timestamp && it.exportStatus == ProcessingPhoto.ExportStatus.SENT }
                        .isNullOrEmpty()) {
                    WorkManager.getInstance(applicationContext).cancelAllWorkByTag(taskTag)
                }
                return Result.retry()
            }
        }
        else return Result.retry()

    }
}
