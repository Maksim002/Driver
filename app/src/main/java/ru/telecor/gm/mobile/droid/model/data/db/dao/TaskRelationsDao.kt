package ru.telecor.gm.mobile.droid.model.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
@Dao
interface TaskRelationsDao {
    @Transaction
    @Query("select * from tasks ORDER BY `order` ASC")
    suspend fun getAll(): List<TaskRelations>
}