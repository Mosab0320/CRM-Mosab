package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.ClientNote

@Dao
interface ClientNoteDao {
    @Query("SELECT * FROM client_notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<ClientNote>>

    @Query("SELECT * FROM client_notes WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getNotesByLead(leadId: Long): LiveData<List<ClientNote>>

    @Query("SELECT * FROM client_notes WHERE leadId = :leadId ORDER BY timestamp DESC")
    suspend fun getNotesByLeadSync(leadId: Long): List<ClientNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: ClientNote): Long

    @Update
    suspend fun update(note: ClientNote)

    @Delete
    suspend fun delete(note: ClientNote)

    @Query("DELETE FROM client_notes WHERE leadId = :leadId")
    suspend fun deleteByLeadId(leadId: Long)
}
