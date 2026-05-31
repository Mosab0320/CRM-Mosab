package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.CrmUnit

@Dao
interface UnitDao {
    @Query("SELECT * FROM units ORDER BY id DESC")
    fun getAllUnits(): LiveData<List<CrmUnit>>

    @Query("SELECT * FROM units WHERE project = :project ORDER BY id DESC")
    fun getUnitsByProject(project: String): LiveData<List<CrmUnit>>

    @Query("SELECT * FROM units WHERE status = :status ORDER BY id DESC")
    fun getUnitsByStatus(status: String): LiveData<List<CrmUnit>>

    @Query("SELECT * FROM units WHERE type = :type ORDER BY id DESC")
    fun getUnitsByType(type: String): LiveData<List<CrmUnit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: CrmUnit): Long

    @Update
    suspend fun update(unit: CrmUnit)

    @Delete
    suspend fun delete(unit: CrmUnit)

    @Query("DELETE FROM units")
    suspend fun deleteAll()
}
