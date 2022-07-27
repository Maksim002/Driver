package ru.telecor.gm.mobile.droid.utils

import android.content.Context
import android.graphics.*
import ru.telecor.gm.mobile.droid.model.data.storage.AppPreferences
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

lateinit var imageFile: File
lateinit var newImage: Bitmap

fun bitmapConvertor(tempFile: File, partOne: String, partTwo: String, date: String,  getLongitude: Float, getWidth: Float, context: Context? = null): Bitmap{
    val appPreferences = AppPreferences(context!!)

    val bitmap = BitmapFactory.decodeFile(tempFile.path)

    val config: Bitmap.Config = bitmap.config
    val width = bitmap.width
    val height = bitmap.height

    newImage = Bitmap.createBitmap(width, height, config)

    val c = Canvas(newImage)
    c.drawBitmap(bitmap, 0f, 0f, null)

    val paint = Paint()
    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    paint.textSize = 23f

    if (partOne != "999" || partTwo != "999") {
        if (appPreferences.isTimeCheck && !appPreferences.isLocationCheck && appPreferences.isDateCheck) {
            if (partTwo != "") {
                c.drawText(date, 17f, height.toFloat() - 81, paint)
                c.drawText(partOne, 17f, height.toFloat() - 54, paint)
                c.drawText(partTwo, 10f, height.toFloat() - 27, paint)
            } else {
                c.drawText(date, 17f, height.toFloat() - 54, paint)
                c.drawText(partOne, 17f, height.toFloat() - 27, paint)
            }
        } else if (!appPreferences.isTimeCheck && appPreferences.isLocationCheck && appPreferences.isDateCheck) {
            c.drawText(date, 17f, height.toFloat() - 54, paint)
            c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 27, paint)
        } else if (!appPreferences.isTimeCheck && !appPreferences.isLocationCheck && appPreferences.isDateCheck) {
            c.drawText(date, 17f, height.toFloat() - 27, paint)
        } else if (!appPreferences.isTimeCheck && appPreferences.isLocationCheck && !appPreferences.isDateCheck) {
            c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 27, paint)
        } else if (appPreferences.isTimeCheck && !appPreferences.isLocationCheck && !appPreferences.isDateCheck) {
            if (partTwo != "") {
                c.drawText(partOne, 17f, height.toFloat() - 54, paint)
                c.drawText(partTwo, 10f, height.toFloat() - 27, paint)
            } else {
                c.drawText(partOne, 17f, height.toFloat() - 27, paint)
            }
        } else {
            if (appPreferences.isDateCheck) {
                if (partTwo != "") {
                    c.drawText(date, 17f, height.toFloat() - 108, paint)
                } else {
                    c.drawText(date, 17f, height.toFloat() - 81, paint)
                }
            }
            if (appPreferences.isLocationCheck) {
                if (partTwo != "") {
                    c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 81, paint)
                } else {
                    c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 54, paint)
                }
            }
            if (appPreferences.isTimeCheck) {
                if (partTwo != "") {
                    c.drawText(partOne, 17f, height.toFloat() - 54, paint)
                    c.drawText(partTwo, 10f, height.toFloat() - 27, paint)
                } else {
                    c.drawText(partOne, 17f, height.toFloat() - 27, paint)
                }
            }
        }
    }else{
        if (appPreferences.isDateCheck && appPreferences.isLocationCheck){
            c.drawText(date, 17f, height.toFloat() - 54, paint)
            c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 27, paint)
        }else{
          if (appPreferences.isDateCheck && !appPreferences.isLocationCheck) {
              c.drawText(date, 17f, height.toFloat() - 27, paint)
          }
            if (!appPreferences.isDateCheck && appPreferences.isLocationCheck){
                c.drawText("$getLongitude - $getWidth", 17f, height.toFloat() - 27, paint)
            }
        }
    }

    return newImage
}

fun persistImage(bitmap: Bitmap, name: String, context: Context): File {
    val filesDir: File = context.filesDir
    imageFile = File(filesDir, name)
    val os: OutputStream
    try {
        os = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.flush()
        os.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return imageFile
}