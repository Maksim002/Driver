package ru.telecor.gm.mobile.droid.model.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.model.data.db.converters.DataConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.EnumConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.ListDataConverters
import ru.telecor.gm.mobile.droid.model.data.db.dao.*


@Database(
    entities = [
        (ProcessingPhoto::class),
        (TaskExtended::class),
        (TaskDraftProcessingResult::class),
        (TaskProcessingResult::class)
    ],
    version = 7,
    exportSchema = true
)

@TypeConverters(
    EnumConverters::class,
    DataConverters::class,
    ListDataConverters::class
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun processingPhotoDao(): ProcessingPhotoDao
    abstract fun taskExtendedDao(): TaskExtendedDao
    abstract fun taskProcessingResultDao(): TaskProcessingResultDao
    abstract fun taskDraftProcessingResultDao(): TaskDraftProcessingResultDao
    abstract fun taskRelationsDaoDao(): TaskRelationsDao


}