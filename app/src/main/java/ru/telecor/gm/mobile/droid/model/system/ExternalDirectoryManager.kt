package ru.telecor.gm.mobile.droid.model.system

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.system.Os.bind
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.io.File
import java.io.FileNotFoundException
import java.sql.Array
import javax.inject.Inject

class ExternalDirectoryManager @Inject constructor(private val context: Context) :
    EDirectoryManager {

    companion object {
        const val appDirectoryName = "truckCrew"
    }

    override val photoPath: String
        get() = "${getStorageDirectory()?.absolutePath}${File.separator}${
            generateDirectory(
                arrayListOf(
                    appDirectoryName, "files", "Pictures"
                )
            )
        }" ?: "error"

    private fun getStorageDirectory(): File? {
        return Environment.getExternalStorageDirectory()
    }

    private fun generateDirectory(arr: ArrayList<String>): String? {
        var result: String? = null

        for(ar in arr){
            result = if (result == null) ar else "${result}${File.separator}${ar}"
            val dir = File(Environment.getExternalStorageDirectory(), result)
            if (!dir.exists()) {
                dir.mkdir()
            }
        }

        return result
    }

    override val filesPath: File
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    override val cacheDirectory: File
        get() = context.externalCacheDir ?: throw FileNotFoundException()
}
