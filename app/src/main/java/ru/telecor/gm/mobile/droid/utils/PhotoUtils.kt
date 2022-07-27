package ru.telecor.gm.mobile.droid.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okio.IOException
import ru.telecor.gm.mobile.droid.FIREBASE_REPORT_TYPE_KEY
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

object PhotoUtils {

    const val maxImageSize = 1000f

    private fun getImageOrientation(imagePath: String): Int {
        var rotate = 0
        try {
            val exif = ExifInterface(imagePath)
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotate
    }

    fun tryPhotoResize(file: File , photoHeight: Int, photoWidth: Int) {
        try {
            val bm: Bitmap? = BitmapFactory.decodeFile(file.absolutePath)
            val bitmap = bm ?: throw Exception("bitmap is null")

            if (bitmap.height >= photoHeight && bitmap.width >= photoWidth) {
                val rotation = getImageOrientation(file.absolutePath)
                val newBitmap = scaleDown(bitmap, maxImageSize, true)
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                val rotatedBitmap =
                    Bitmap.createBitmap(
                        newBitmap,
                        0,
                        0,
                        newBitmap.width,
                        newBitmap.height,
                        matrix,
                        true
                    )
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file, false))
            }
        } catch (exception: Exception) {
            Timber.e("rescale unsuccessful. Operation aborted")
            Timber.e(exception)
            FirebaseCrashlytics.getInstance()
                .setCustomKey(FIREBASE_REPORT_TYPE_KEY, "Photo rescale is unsuccessful")
            FirebaseCrashlytics.getInstance().recordException(exception)
        }
    }

    private fun scaleDown(
        realImage: Bitmap,
        maxImageSize: Float,
        filter: Boolean
    ): Bitmap {
        val ratio = min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    }

    fun stringToObject(file: String): Bitmap {
        val imageBytes = Base64.decode(file, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
