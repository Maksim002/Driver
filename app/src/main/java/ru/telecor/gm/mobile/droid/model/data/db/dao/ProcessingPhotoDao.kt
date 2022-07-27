package ru.telecor.gm.mobile.droid.model.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto

@Dao
interface ProcessingPhotoDao {

    // get
    @Query("SELECT * FROM processing_photos")
    fun getAll(): List<ProcessingPhoto>

    @Query("SELECT * FROM processing_photos")
    fun getAllInLive(): LiveData<List<ProcessingPhoto>>

    @Query("SELECT * FROM processing_photos WHERE id = :id")
    fun getById(id: Long): ProcessingPhoto

    @Query("SELECT * FROM processing_photos WHERE routeId = :routeId")
    fun getByRouteId(routeId: Long): List<ProcessingPhoto>

    @Query("SELECT * FROM processing_photos WHERE taskId = :taskId")
    fun getByTaskId(taskId: Long): ProcessingPhoto

    @Query("SELECT * FROM processing_photos WHERE routeId = :routeId AND taskId = :taskId AND timestamp = :timestamp LIMIT 1 ")
    fun search(routeId: Long,taskId: Long,timestamp: String): ProcessingPhoto?


    // create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(photo: ProcessingPhoto)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(photos: List<ProcessingPhoto>)


    //Update
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(photo: ProcessingPhoto)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAll(photo: ProcessingPhoto)


    // Delete
    @Delete
    fun delete(photo: ProcessingPhoto)

    @Delete
    fun deleteAll(photos: List<ProcessingPhoto>)
}