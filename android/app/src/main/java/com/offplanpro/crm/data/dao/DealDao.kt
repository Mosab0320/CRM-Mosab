package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.Deal

@Dao
interface DealDao {
    @Query("SELECT * FROM deals ORDER BY id DESC")
    fun getAllDeals(): LiveData<List<Deal>>

    @Query("SELECT * FROM deals ORDER BY id DESC")
    suspend fun getAllDealsList(): List<Deal>

    @Query("SELECT COUNT(*) FROM deals WHERE status = 'مكتملة'")
    suspend fun getCompletedDealsCount(): Int

    @Query("SELECT SUM(myComm) FROM deals WHERE status != 'ملغاة'")
    suspend fun getTotalCommission(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deal: Deal): Long

    @Update
    suspend fun update(deal: Deal)

    @Delete
    suspend fun delete(deal: Deal)

    @Query("DELETE FROM deals")
    suspend fun deleteAll()
}
