package ru.telecor.gm.mobile.droid.model.data.db.converters

import androidx.room.TypeConverter
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.processing.TaskFinishType
import ru.telecor.gm.mobile.droid.model.PhotoType

class EnumConverters {

    @TypeConverter
    fun toPhotoType(value: Int) = enumValues<PhotoType>()[value]
    @TypeConverter
    fun fromPhotoType(value: PhotoType) = value.ordinal

    @TypeConverter
    fun toExportStatus(value: Int) = enumValues<ProcessingPhoto.ExportStatus>()[value]
    @TypeConverter
    fun fromExportStatus(value: ProcessingPhoto.ExportStatus) = value.ordinal

    @TypeConverter
    fun toStatusType(value: Int) = enumValues<StatusType>()[value]
    @TypeConverter
    fun fromStatusType(value: StatusType) = value.ordinal

    @TypeConverter
    fun toTaskFinishType(value: Int) = enumValues<TaskFinishType>()[value]
    @TypeConverter
    fun fromTaskFinishType(value: TaskFinishType) = value.ordinal

    @TypeConverter
    fun toProcessingStatus(value: Int) = enumValues<TaskProcessingResult.ProcessingStatus>()[value]
    @TypeConverter
    fun fromProcessingStatus(value: TaskProcessingResult.ProcessingStatus) = value.ordinal

}