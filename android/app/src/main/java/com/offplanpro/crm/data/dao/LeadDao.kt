package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.Lead

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY id DESC")
    fun getAllLeads(): LiveData<List<Lead>>

    @Query("SELECT * FROM leads ORDER BY id DESC")
    suspend fun getAllLeadsList(): List<Lead>

    @Query("SELECT * FROM leads WHERE heat = 'hot' ORDER BY id DESC")
    fun getHotLeads(): LiveData<List<Lead>>

    @Query("SELECT * FROM leads WHERE stage = :stage ORDER BY id DESC")
    fun getLeadsByStage(stage: String): LiveData<List<Lead>>

    @Query("SELECT * FROM leads WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchLeads(query: String): LiveData<List<Lead>>

    @Query("SELECT COUNT(*) FROM leads")
    suspend fun getLeadsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lead: Lead): Long

    @Update
    suspend fun update(lead: Lead)

    @Delete
    suspend fun delete(lead: Lead)

    @Query("DELETE FROM leads")
    suspend fun deleteAll()
}
