package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.FollowUp

@Dao
interface FollowUpDao {
    @Query("SELECT * FROM followups ORDER BY date DESC")
    fun getAllFollowUps(): LiveData<List<FollowUp>>

    @Query("SELECT * FROM followups WHERE status = 'pending' ORDER BY nextDate ASC")
    fun getPendingFollowUps(): LiveData<List<FollowUp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(followUp: FollowUp): Long

    @Update
    suspend fun update(followUp: FollowUp)

    @Delete
    suspend fun delete(followUp: FollowUp)

    @Query("DELETE FROM followups")
    suspend fun deleteAll()
}
