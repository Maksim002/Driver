package ru.telecor.gm.mobile.droid.model.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `task_draft_processing_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeId` INTEGER NOT NULL, `statusType` TEXT NOT NULL, `failureReason` TEXT, `actualReason` TEXT, `standResults` TEXT, `detourPointProcessingResults` TEXT, `hasPhotos` INTEGER NOT NULL, `photosCount` INTEGER, `visitPointId` TEXT, `taskFinishType` INTEGER NOT NULL)")
    }
}
//val MIGRATION_7_8 = object : Migration(7, 8) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE `processing_photos` ADD COLUMN `timeDate` TEXT DEFAULT null")
//    }
//}