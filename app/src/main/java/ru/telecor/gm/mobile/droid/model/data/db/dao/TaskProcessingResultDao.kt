package ru.telecor.gm.mobile.droid.model.data.db.dao

import androidx.room.*
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult

@Dao
interface TaskProcessingResultDao {

    // get
    @Query("SELECT * FROM task_processing_results")
    fun getAll(): List<TaskProcessingResult>?

    @Query("SELECT * FROM task_processing_results WHERE id = :id")
    fun getById(id: Long): TaskProcessingResult?

    @Query("SELECT * FROM task_processing_results WHERE routeId = :routeId")
    fun getAllByRouteId(routeId: Long): List<TaskProcessingResult>

    @Query("SELECT * FROM task_processing_results WHERE processingStatus = :processingStatus")
    fun getAllByStatus(processingStatus: TaskProcessingResult.ProcessingStatus): List<TaskProcessingResult>

    @Query("UPDATE task_processing_results SET processingStatus = :processingStatus")
    fun updateAllProcessingStatus(processingStatus: TaskProcessingResult.ProcessingStatus)

    @Query("SELECT * FROM task_processing_results WHERE id = :id AND processingStatus = :processingStatus")
    fun getByIdAndProcessingStatus(id: Long,processingStatus: TaskProcessingResult.ProcessingStatus): TaskProcessingResult?

    // create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskProcessingResult: TaskProcessingResult)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskProcessingResults: List<TaskProcessingResult>)

    //Update
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(taskProcessingResult: TaskProcessingResult)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(taskProcessingResults: List<TaskProcessingResult>)

    // Delete
    @Delete
    fun delete(taskProcessingResult: TaskProcessingResult)
    @Delete
    fun deleteAll(taskProcessingResults: List<TaskProcessingResult>)

    //Clean
    @Query("DELETE FROM task_processing_results")
    fun cleanTable()
}