package ru.telecor.gm.mobile.droid.utils


import android.os.Build
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.system.EDirectoryManager
import ru.telecor.gm.mobile.droid.model.system.IDirectoryManager
import java.io.File
import java.util.*
import javax.inject.Inject

class DataStorageManager @Inject constructor(
    private val internalDirectory: IDirectoryManager,
    private val externalDirectory: EDirectoryManager,
    private val settingsPrefs: SettingsPrefs,
) {

    companion object {

        const val ACTUAL_DIR = "actual"
        const val TEMP_DIR = "taskTemp"
        const val ARCHIVE_DIR = "archive"
        const val NEW_VERSION = "newApkVersion"
    }

    fun getMainDirectory(): String = when (Build.VERSION.SDK_INT) {
        in 1..29 -> externalDirectory.photoPath
        else -> internalDirectory.photoPath
    }

    fun getCachePhotoDir(): String = externalDirectory.cacheDirectory.toString()

    fun getNewVersionDirectory(): String = "${getMainDirectory()}/${NEW_VERSION}"

    fun getActualPhotosDir(): String = "${getMainDirectory()}/${ACTUAL_DIR}"


    fun getTempPhotosDir(): String = "${getMainDirectory()}/${TEMP_DIR}"


    fun getArchivePhotosDir(): String = "${getMainDirectory()}/${ARCHIVE_DIR}"

    fun getCacheDirectory(): File = when (settingsPrefs.isInternalStorage) {
        false -> externalDirectory.cacheDirectory
        else -> internalDirectory.cacheDirectory
    }


}