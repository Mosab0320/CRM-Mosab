package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.ActivityItem

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY id DESC LIMIT 20")
    fun getRecentActivities(): LiveData<List<ActivityItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityItem): Long

    @Query("DELETE FROM activities WHERE id NOT IN (SELECT id FROM activities ORDER BY id DESC LIMIT 50)")
    suspend fun trimActivities()

    @Query("DELETE FROM activities")
    suspend fun deleteAll()
}
