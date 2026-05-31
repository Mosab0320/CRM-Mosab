package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.CallLogEntry

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): LiveData<List<CallLogEntry>>

    @Query("SELECT * FROM call_logs WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getCallLogsByLead(leadId: Long): LiveData<List<CallLogEntry>>

    @Query("SELECT * FROM call_logs WHERE leadId = :leadId ORDER BY timestamp DESC")
    suspend fun getCallLogsByLeadSync(leadId: Long): List<CallLogEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(callLog: CallLogEntry): Long

    @Update
    suspend fun update(callLog: CallLogEntry)

    @Delete
    suspend fun delete(callLog: CallLogEntry)

    @Query("DELETE FROM call_logs WHERE leadId = :leadId")
    suspend fun deleteByLeadId(leadId: Long)
}
