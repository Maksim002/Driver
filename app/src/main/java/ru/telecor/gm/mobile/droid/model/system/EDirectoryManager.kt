package ru.telecor.gm.mobile.droid.model.system

import java.io.File

interface EDirectoryManager {

    val photoPath: String
    val filesPath: File
    val cacheDirectory: File
}
