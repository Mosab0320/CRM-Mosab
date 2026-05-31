package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.CrmTask

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC, id DESC")
    fun getAllTasks(): LiveData<List<CrmTask>>

    @Query("SELECT * FROM tasks WHERE done = 0 ORDER BY date ASC")
    fun getPendingTasks(): LiveData<List<CrmTask>>

    @Query("SELECT * FROM tasks WHERE done = 1 ORDER BY id DESC")
    fun getDoneTasks(): LiveData<List<CrmTask>>

    @Query("SELECT * FROM tasks WHERE done = 0 AND date <= :today ORDER BY date ASC")
    suspend fun getOverdueTasks(today: String): List<CrmTask>

    @Query("SELECT * FROM tasks WHERE done = 0 ORDER BY date ASC LIMIT 5")
    suspend fun getUpcomingTasks(): List<CrmTask>

    @Query("SELECT COUNT(*) FROM tasks WHERE done = 0 AND date < :today")
    suspend fun getOverdueCount(today: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: CrmTask): Long

    @Update
    suspend fun update(task: CrmTask)

    @Delete
    suspend fun delete(task: CrmTask)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
