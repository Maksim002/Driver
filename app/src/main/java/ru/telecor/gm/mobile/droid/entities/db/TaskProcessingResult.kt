package ru.telecor.gm.mobile.droid.entities.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.processing.TaskFinishType
import ru.telecor.gm.mobile.droid.model.data.db.converters.DataConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.EnumConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.ListDataConverters

@Entity(tableName = "task_processing_results")
data class TaskProcessingResult(
    @Expose
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @Expose
    @SerializedName("routeId")
    val routeId: Long,

    @Expose
    @SerializedName("statusType")
    @TypeConverters(DataConverters::class)
    val statusType: ProcessingStatusType,

    @Expose
    @SerializedName("failureReason")
    @TypeConverters(DataConverters::class)
    val failureReason: TaskFailureReason?,

    @Expose
    @SerializedName("actualReason")
    val actualReason: String?,

    @Expose
    @SerializedName("standResults")
    @TypeConverters(ListDataConverters::class)
    val standResults: List<StandResult>?,

    @Expose
    @SerializedName("detourPointProcessingResults")
    @TypeConverters(ListDataConverters::class)
    val detourPointProcessingResults: List<Any>?,

    @Expose
    @SerializedName("hasPhotos")
    val hasPhotos: Boolean = false,

    @Expose
    @SerializedName("photosCount")
    val photosCount: Int?,

    @Expose
    @SerializedName("visitPointId")
    val visitPointId: String?,

    @Expose
    @SerializedName("taskFinishType")
    val taskFinishType: TaskFinishType,

    @Expose
    @SerializedName("processingStatus")
    @TypeConverters(EnumConverters::class)
    val processingStatus: ProcessingStatus = ProcessingStatus.PROCESSING
) {
    enum class ProcessingStatus {
        DELIVERED,
        PROCESSING
    }
}

