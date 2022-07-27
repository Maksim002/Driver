package ru.telecor.gm.mobile.droid.entities.db

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.ActualReason
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.data.db.converters.EnumConverters
import java.io.File
import java.text.SimpleDateFormat
import android.graphics.Bitmap

import androidx.room.ColumnInfo

import androidx.room.PrimaryKey




@Entity(tableName = "processing_photos", indices = [Index(value = ["routeId", "taskId","timestamp"], unique = true)])

data class ProcessingPhoto(
    @Expose
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @Expose
    @SerializedName("routeId")
    var routeId: Long,

    @Expose
    @SerializedName("taskId")
    var taskId: Long,

    @Expose
    @SerializedName("photoPath")
    var photoPath: String,

    @Expose
    @SerializedName("photoType")
    @TypeConverters(EnumConverters::class)
    var photoType: PhotoType,

    @Expose
    @SerializedName("latitude")
    val latitude: Double = 0.0,

    @Expose
    @SerializedName("longitude")
    val longitude: Double = 0.0,

    @Expose
    @SerializedName("timestamp")
    val timestamp: String,

    @Expose
    @SerializedName("exportStatus")
    @TypeConverters(EnumConverters::class)
    var exportStatus: ExportStatus = ExportStatus.READY,

    @Expose
    @SerializedName("cachePath")
    val cachePath: String? = null,

    @Expose
    @SerializedName("uploadingTime")
    val  uploadingTime: String? = null,

    @Expose
    @SerializedName("httpCode")
    val  httpCode: String? = null
    )
{
    companion object {
        fun fromJson(string: String): ProcessingPhoto {
            return Gson().fromJson(string, ProcessingPhoto::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    enum class ExportStatus {
        TEMP,
        READY,
        SENT
    }
}
data class PhotoProcessingForApi(
    @SerializedName("actualReason")
    val actualReason: ActualReason? = null,
    @SerializedName("filename")
    val filename: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("time")
    val time: String,
    @SerializedName("type")
    val type: String
) {
    companion object {
        fun fromProcessingPhoto(photo: ProcessingPhoto): PhotoProcessingForApi {
            val reason = when (photo.photoType) {
                PhotoType.LOAD_TROUBLE_BLOCKAGE -> ActualReason.BULK
                PhotoType.LOAD_TROUBLE -> ActualReason.CONTAINER_FAILURE
                else -> null
            }
            val file = File(photo.photoPath)

            return PhotoProcessingForApi(
                actualReason = reason,
                filename = file.name,
                size = file.length(),
                latitude = photo.latitude,
                longitude = photo.longitude,
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(photo.timestamp.toLong()),
                type = photo.photoType.serverName
            )
        }
    }
}

