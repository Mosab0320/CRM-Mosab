package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.Goal

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE id = 1")
    fun getGoals(): LiveData<Goal?>

    @Query("SELECT * FROM goals WHERE id = 1")
    suspend fun getGoalsSync(): Goal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)
}
