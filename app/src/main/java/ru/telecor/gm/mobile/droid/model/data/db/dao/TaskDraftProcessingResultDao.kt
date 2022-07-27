package ru.telecor.gm.mobile.droid.model.data.db.dao

import androidx.room.*
import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult

@Dao
interface TaskDraftProcessingResultDao {

    // get
    @Query("SELECT * FROM task_draft_processing_results")
    suspend fun getAll(): List<TaskDraftProcessingResult>?

    @Query("SELECT * FROM task_draft_processing_results WHERE id = :id")
    suspend fun getById(id: Long): TaskDraftProcessingResult?

    @Query("SELECT * FROM task_draft_processing_results WHERE routeId = :routeId")
    suspend fun getAllByRouteId(routeId: Long): List<TaskDraftProcessingResult>

    // create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskProcessingResult: TaskDraftProcessingResult)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taskProcessingResults: List<TaskDraftProcessingResult>)

    // Delete
    @Delete
    suspend fun delete(taskProcessingResult: TaskDraftProcessingResult)
    @Delete
    suspend fun deleteAll(taskProcessingResults: List<TaskDraftProcessingResult>)

    //Clean
    @Query("DELETE FROM task_draft_processing_results")
    suspend fun cleanTable()
}