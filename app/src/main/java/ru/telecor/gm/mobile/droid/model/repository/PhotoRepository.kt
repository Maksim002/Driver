package ru.telecor.gm.mobile.droid.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import ru.telecor.gm.mobile.droid.FIREBASE_REPORT_TYPE_KEY
import ru.telecor.gm.mobile.droid.entities.ActualReason
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.data.db.dao.ProcessingPhotoDao
import ru.telecor.gm.mobile.droid.model.data.db.dao.TaskExtendedDao
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.ServerError
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.storage.AppPreferences
import ru.telecor.gm.mobile.droid.model.system.LocationProvider
import ru.telecor.gm.mobile.droid.model.workers.UploadWorker
import ru.telecor.gm.mobile.droid.utils.*
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

class PhotoRepository @Inject constructor(
    private val context: Context,
    private val dataStorageManager: DataStorageManager,
    private val appPreferences: AppPreferences,
    private val api: TruckCrewApi,
    private val processingPhotoDao: ProcessingPhotoDao,
    private val taskExtendedDao: TaskExtendedDao,
    val locationProvider: LocationProvider,
) : BaseRepository() {

    val TAG = javaClass.simpleName

    private val _allPhotosFlow = MutableStateFlow<List<ProcessingPhoto>>(listOf())
    val allPhotosFlow: StateFlow<List<ProcessingPhoto>> = _allPhotosFlow
    var keyPhotoName = MutableLiveData<Boolean>()


    private val cachePhotoDirectory = File(dataStorageManager.getCachePhotoDir())
    private val actualPhotoDirectory = File(dataStorageManager.getActualPhotosDir())
    private val archivePhotoDirectory = File(dataStorageManager.getArchivePhotosDir())
    private val tempPhotoDirectory = File(dataStorageManager.getTempPhotosDir())


    init {

        if (!actualPhotoDirectory.exists()) {
            actualPhotoDirectory.mkdir()
        }

        if (!archivePhotoDirectory.exists()) {
            archivePhotoDirectory.mkdir()
        }

        if (!tempPhotoDirectory.exists()) {
            tempPhotoDirectory.mkdir()
        }

        if (!cachePhotoDirectory.exists()) {
            cachePhotoDirectory.mkdir()
        }



        updaterPhotoLists()

    }

    private fun updaterPhotoLists() = CoroutineScope(Dispatchers.IO).launch {
        _allPhotosFlow.value = processingPhotoDao.getAll()
    }

     fun addPhoto(
        routeId: Long,
        taskId: Long,
        photoType: PhotoType,
        latitude: Double,
        longitude: Double,
        file: File,
        timeStamp: String = Date().time.toString(),
        isExportable: Boolean = false,
        usResize: Boolean = true
    ): ProcessingPhoto? {
        try{
            if (usResize) PhotoUtils.tryPhotoResize(
                file,
                appPreferences.photoHeight.toInt(),
                appPreferences.photoWidth.toInt()
            )

            val newFile = if (keyPhotoName.value == false && !usResize) {
                file
            } else {
                persistImage(
                    bitmapConvertor(
                        file,
                        photoType.viewName,
                        "",
                        dateTime(),
                        latitude.toFloat(),
                        longitude.toFloat(),
                        context
                    ), file.name, context
                )
            }
            GlobalScope.launch {
                cleanUpTempDirectory(taskId)
            }

            //Photo file creation
            val photoFile = if (isExportable) {
                createPhotoFileByParameters(routeId, taskId, photoType, timeStamp, actualPhotoDirectory)
            } else {
                createPhotoFileByParameters(routeId, taskId, photoType, timeStamp, tempPhotoDirectory)
            }

            if (newFile.absolutePath != photoFile.absolutePath) {
                if ((photoFile.length() / 1024).toString().toInt() <= 12) newFile.copyTo(
                    photoFile,
                    true
                )
            }

        val metaObject =
            ProcessingPhoto(
                routeId = routeId,
                taskId = taskId,
                photoPath = photoFile.absolutePath,
                photoType = photoType,
                latitude = latitude,
                longitude = longitude,
                timestamp = timeStamp,
                exportStatus = if (isExportable) {
                    ProcessingPhoto.ExportStatus.READY
                } else {
                    ProcessingPhoto.ExportStatus.TEMP
                },
                cachePath = file.absolutePath
            )

            CoroutineScope(Dispatchers.IO).launch {
                processingPhotoDao.insert(metaObject)
                updaterPhotoLists()
            }

            // TODO: 04.02.2022 Здесь я очищаю фаел и битмап
            //imageFile.delete()
            //newImage.recycle()

            return metaObject
        }catch (e: Exception){
            Timber.e(e)
            return  null
        }
    }

    suspend fun cleanUpTempDirectory(taskId: Long? = null) {
        var routeTasksId = taskExtendedDao.getAllId()?.map { it.toString() } ?: arrayListOf()
        if (taskId != null) routeTasksId = (routeTasksId + taskId.toString()).distinct()
        val filesList = tempPhotoDirectory.listFiles()
        for (file in filesList) {
            if (file.isFile) {
                val arr = file.name.split(File.pathSeparatorChar, '_', '.')
                if (arr[1] !in routeTasksId) {
                    val photo = allPhotosFlow.value?.find { it.photoPath == file.absolutePath }
                    if (photo != null) {
                        deletePhoto(photo, withCache = false)
                    } else {
                        file.delete()
                    }
                }
            }
        }
    }

    suspend fun setPhotoExportable(photo: ProcessingPhoto) {
        val moved = movePhotoFile(photo, actualPhotoDirectory)
        val new = moved.copy(
            exportStatus = ProcessingPhoto.ExportStatus.READY,
        )
        replaceMetaData(new)
        scheduleUploading(new)
    }

    private fun setPhotoExported(photo: ProcessingPhoto) {
        val moved = movePhotoFile(photo, archivePhotoDirectory)

        val new = moved.copy(
            exportStatus = ProcessingPhoto.ExportStatus.SENT,
            uploadingTime = Date().time.toString(),
            httpCode = "200"
        )
        replaceMetaData(new)
    }

    private fun setPhotoNonExported(photo: ProcessingPhoto, code: String = "500") {
        //val moved = movePhotoFile(photo, archivePhotoDirectory)
        val new = photo.copy(
            uploadingTime = Date().time.toString(),
            httpCode = code
        )
        replaceMetaData(new)
    }


    private fun replaceMetaData(new: ProcessingPhoto) = runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            processingPhotoDao.update(new)
            updaterPhotoLists()
        }
    }

    private fun movePhotoFile(photo: ProcessingPhoto, directory: File): ProcessingPhoto {
        if (!directory.isDirectory) throw IllegalArgumentException("directory file must be a directory!")
        val photoFile = File(photo.photoPath)
        val targetFile = File(directory.absolutePath + File.separator + photoFile.name)
        Thread.sleep(99)
        if (targetFile.exists() && targetFile.absolutePath == photo.photoPath) {
            return photo
        } else {
            if (photoFile.exists()) {
                try {
                    targetFile.createNewFile()
                    photoFile.copyTo(targetFile, true)
                    photoFile.delete()
                } catch (e: Exception) {
                    LogUtils.error(TAG, e.message)
                }

            } else {
                LogUtils.error(TAG, "try duplicate task result ${directory.absolutePath}")
            }
        }

        return photo.copy(photoPath = targetFile.absolutePath)
    }

    private fun createPhotoFileByParameters(
        routeId: Long,
        taskId: Long,
        photoType: PhotoType,
        timeStamp: String,
        directory: File,
    ): File {

        if (!directory.isDirectory) throw IllegalArgumentException("directory file must be a directory!")

        val file =
            File("${directory.path}${File.separator}${routeId}${File.pathSeparator}${taskId}${File.pathSeparator}${photoType.photoTag}_${timeStamp}.jpg")

        if (file.exists()) file.delete()

        file.createNewFile()

        return file
    }

    suspend fun scheduleUploading(photo: ProcessingPhoto) {
        if (!checkPhotoCrash(photo.photoPath)) {

            val taskTag = String.format("%s_%d_%d", photo.timestamp, photo.routeId, photo.taskId)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadWorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
                .setInputData(workDataOf("PHOTO_INPUT" to photo.toJson()))
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(taskTag)
                .build()

            WorkManager.getInstance(context)
                .enqueue(uploadWorkRequest)
        } else {
            CoroutineScope(Dispatchers.IO).launch { tryMigrateNonMigratedFiles() }
        }
    }

    fun getAvailabilityToUpload(): Boolean {
        return ConnectivityUtils.syncAvailability(context, ConnectivityUtils.DataType.FILE)
    }

    suspend fun uploadPhoto(photo: ProcessingPhoto): Result<ResponseBody> {
        val res = uploadPhoto(
            file = File(photo.photoPath),
            photoType = photo.photoType,
            latitude = photo.latitude,
            longitude = photo.longitude,
            taskId = photo.taskId
        )

        when (res) {
            is Result.Success -> {
                setPhotoExported(photo)
            }
            is Result.Error -> {
                GlobalScope.launch {
                    val code = if (res.exception is ServerError) res.exception.errorCode.toString() else "408"
                    replaceMetaData(photo.copy(uploadingTime = Date().time.toString(), httpCode = code))
                }

                FirebaseCrashlytics.getInstance()
                    .setCustomKey(FIREBASE_REPORT_TYPE_KEY, "Photo is not sent")
                FirebaseCrashlytics.getInstance().recordException(res.exception)
            }
        }
        return res
    }

    private suspend fun uploadPhoto(
        file: File, photoType: PhotoType, latitude: Double,
        longitude: Double, taskId: Long,
    ): Result<ResponseBody> = safeApiCall {
        val body = MultipartBody.Part.create(file.asRequestBody("image/jpeg".toMediaTypeOrNull()))

        val builder = MultipartBody.Builder().apply {
            setType(MultipartBody.FORM)
            addFormDataPart("file", file.absolutePath, body.body)
            addFormDataPart("photoType", photoType.serverName)
            addFormDataPart("latitude", latitude.toString())
            addFormDataPart("longitude", longitude.toString())
            addFormDataPart("routeTaskId", taskId.toString())
            when (photoType) {
                PhotoType.LOAD_TROUBLE -> {
                    addFormDataPart("actualReason", ActualReason.CONTAINER_FAILURE.name)
                }
                PhotoType.LOAD_TROUBLE_BLOCKAGE -> {
                    addFormDataPart("actualReason", ActualReason.BULK.name)
                }
                else -> {
                }
            }
        }
        return@safeApiCall api.uploadTaskPhoto(builder.build())
    }

    fun getContext() = context

    suspend fun getUndeliveredPhotosFromList(names: List<String>) =
        safeApiCall {
            api.getUndeliveredPhotosFromList(names)
        }


    suspend fun recoveryMetadata() = recoveryMetadataSyn()

    suspend fun tryMigrateNonMigratedFiles() =
        allPhotosFlow.value?.let {
            recoveryPhotoFiles(it.filter {
                it.exportStatus != ProcessingPhoto.ExportStatus.SENT
            })
        }

    suspend fun cleanOldPhotos() {
        getCalculatedDate("yyyy-MM-dd", -appPreferences.photoPeriodDelete)?.let { serverDate ->
            val deadlineDate = milliseconds(serverDate)
            allPhotosFlow.value.filter { it.exportStatus == ProcessingPhoto.ExportStatus.SENT }
                .forEach { photo ->
                    val milDate = millisecondsTodDate(photo.timestamp)
                    milDate?.let {
                        val fileDate = milliseconds(it)
                        if (fileDate.toString() <= deadlineDate.toString()) deletePhoto(photo)
                    }
                }
            cleanCacheFilesWithoutMetaData(deadlineDate)
        }

    }


    private fun cleanCacheFilesWithoutMetaData(deadlineDate: Long) {
        val knownPhotos =
            allPhotosFlow.value.filter { it.exportStatus != ProcessingPhoto.ExportStatus.TEMP }

        archivePhotoDirectory.listFiles()
            .filter { it.isFile && knownPhotos.none { knPhoto -> knPhoto.photoPath == it.absolutePath } }
            .forEach { photoFile ->
                val arr = photoFile.name.split(File.pathSeparatorChar, '_', '.')
                if (arr.count() > 3){
                    // Для проверки на дату
                    val milDate = millisecondsTodDate(arr[3])
                    milDate?.let {
                        val fileDate = milliseconds(it)
                        if (fileDate.toString() <= deadlineDate.toString())
                            photoFile.delete()
                    }
                }
            }
//        val knownFile = knownPhotos.mapNotNull { it.cachePath?.split(File.separator)?.last() }
//        cachePhotoDirectory.listFiles()
//            .filter {
//                val format = it.name.split(".").last()
//                it.isFile && it.name !in knownFile && format in arrayOf("jpg", "jpeg", "png", "gif")
//            }
//            .forEach {
//                it.delete()
//            }
    }


    suspend fun deletePhoto(photo: ProcessingPhoto, withCache: Boolean = true) {

        val photoPath = photo.photoPath
        val cachePath = photo.cachePath

        CoroutineScope(Dispatchers.IO).launch {
            processingPhotoDao.delete(photo)
            updaterPhotoLists()
        }

        if (File(photoPath).exists()) File(photoPath).delete()
        //cachePath?.let { if (File(cachePath).exists() && withCache) File(cachePath).delete() }
    }

    fun recoveryMetadataSyn() {
        try {
            val allPhotoFiles: HashMap<ProcessingPhoto.ExportStatus, kotlin.collections.List<File>> =
                hashMapOf()

            actualPhotoDirectory.listFiles()?.let {
                allPhotoFiles.put(
                    ProcessingPhoto.ExportStatus.READY,
                    it.filter { it.isFile })
            }
            archivePhotoDirectory.listFiles()?.let {
                allPhotoFiles.put(
                    ProcessingPhoto.ExportStatus.SENT,
                    it.filter { it.isFile })
            }
            tempPhotoDirectory.listFiles()?.let {
                allPhotoFiles.put(
                    ProcessingPhoto.ExportStatus.TEMP,
                    it.filter { it.isFile })
            }
            for (photos in allPhotoFiles) {
                for (photo in photos.value) {

                    val photoData = photo.name.split(File.pathSeparatorChar, '_', '.')
                    val metaObject =
                        ProcessingPhoto(
                            routeId = photoData[0].toLong(),
                            taskId = photoData[1].toLong(),
                            photoPath = photo.absolutePath,
                            photoType = PhotoType.fromPhotoTag(photoData[2]),
                            timestamp = photoData[3],
                            exportStatus = photos.key
                        )
                    CoroutineScope(Dispatchers.IO).launch {
                        processingPhotoDao.insert(metaObject)
                        updaterPhotoLists()
                    }
                }
            }
        } catch (e: Exception) {
            LogUtils.error(TAG, e.message)
        }
    }
    // Очень важный момент, руки не прочь
    fun checkPhotoCrash(photoPath: String? = null): Boolean {
        return if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            !file.exists() || (file.length() / 1024).toString().toInt() <= 12
        } else true
    }

    suspend fun recoveryPhotoFiles(crashesPhotos: List<ProcessingPhoto>) {
        crashesPhotos.forEach { photo ->
            if (checkPhotoCrash(photo.photoPath)){
                if (!checkPhotoCrash(photo.cachePath))
                    addPhoto(
                        routeId = photo.routeId,
                        taskId = photo.taskId,
                        photoType = photo.photoType,
                        latitude = photo.latitude,
                        longitude = photo.longitude,
                        file = File(photo.cachePath),
                        timeStamp = photo.timestamp,
                        isExportable = true,
                        usResize = false
                    )
                else replaceMetaData(
                    photo.copy(
                        exportStatus = ProcessingPhoto.ExportStatus.SENT,
                        uploadingTime = Date().time.toString(),
                        httpCode = "200"
                    )
                )
            }else{
                scheduleUploading(photo)
            }
        }
    }
}