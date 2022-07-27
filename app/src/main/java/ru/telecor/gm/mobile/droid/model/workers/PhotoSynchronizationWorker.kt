package ru.telecor.gm.mobile.droid.model.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import toothpick.Toothpick
import javax.inject.Inject

class PhotoSynchronizationWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var photoInteractor: PhotoInteractor

    override suspend fun doWork(): Result {
        val appScope = Toothpick.openScope(Scopes.SERVER_SCOPE)
        Toothpick.inject(this, appScope)

        photoInteractor.uploadAllUndeliveredPhotos()

        return Result.success()
    }
}