package ru.telecor.gm.mobile.droid.model.system

import java.io.File

interface IDirectoryManager {

    val photoPath: String
    val filesPath: File
    val cacheDirectory: File
}
