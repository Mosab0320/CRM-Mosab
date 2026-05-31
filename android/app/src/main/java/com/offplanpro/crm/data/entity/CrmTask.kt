package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class CrmTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val type: String = "Call", // Call / Viewing / Meeting / Follow-up / Contracts / Register
    val priority: String = "Normal", // Normal / Important / Urgent
    val date: String = "",
    val client: String = "",
    val project: String = "",
    val notes: String = "",
    val done: Boolean = false
)
