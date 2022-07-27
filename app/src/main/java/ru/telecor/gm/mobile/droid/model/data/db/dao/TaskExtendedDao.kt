package ru.telecor.gm.mobile.droid.model.data.db.dao

import androidx.room.*
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended

@Dao
interface TaskExtendedDao {
    // get
    @Query("SELECT * FROM tasks ORDER BY `order` ASC")
    fun getAll(): List<TaskExtended>?
    @Query("SELECT id FROM tasks ORDER BY `order` ASC")
    fun getAllId(): List<Int>?
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getById(id: Int): TaskExtended?

    @Query("UPDATE tasks SET isCurrent = :isCurrent")
    fun updateIsCurrent(isCurrent :Boolean = false)

    @Query("SELECT COUNT(`id`) FROM tasks")
    fun getRowCount(): Int

    // create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: TaskExtended)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tasks: List<TaskExtended>)

    //Update
    @Update//(onConflict = OnConflictStrategy.IGNORE)
    fun update(task: TaskExtended)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(tasks: List<TaskExtended>)

    // Delete
    @Delete
    suspend fun delete(task: TaskExtended)
    @Delete
    fun deleteAll(tasks: List<TaskExtended>)

    //Clean
    @Query("DELETE FROM tasks")
    fun cleanTable()
}