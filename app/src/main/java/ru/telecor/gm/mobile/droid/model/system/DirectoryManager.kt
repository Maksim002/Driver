package ru.telecor.gm.mobile.droid.model.system

import android.content.Context
import android.os.Environment
import android.system.Os.bind
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class DirectoryManager @Inject constructor(private val context: Context) :IDirectoryManager{

    override val photoPath: String
        get() = getStorageDirectory()?.absolutePath ?: "error"

    private fun getStorageDirectory(): File? {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    override val filesPath: File
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    override val cacheDirectory: File
        get() = context.externalCacheDir ?: throw FileNotFoundException()
}
