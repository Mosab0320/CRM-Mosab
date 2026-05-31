package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leadId: Long = 0,
    val clientName: String = "",
    val duration: String = "", // e.g. "2:35"
    val notes: String = "",
    val outcome: String = "No Answer", // No Answer / Connected / Callback Scheduled / Deal Discussed
    val timestamp: String = "" // ISO datetime
)
