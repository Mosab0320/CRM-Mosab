package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_notes")
data class ClientNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leadId: Long = 0,
    val text: String = "",
    val timestamp: String = "",
    val type: String = "note" // note / stage_change / call / task / deal / attachment
)
