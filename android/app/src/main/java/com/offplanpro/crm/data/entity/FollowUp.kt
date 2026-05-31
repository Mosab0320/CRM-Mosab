package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "followups")
data class FollowUp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val client: String = "",
    val type: String = "",
    val date: String = "",
    val nextDate: String = "",
    val notes: String = "",
    val status: String = "pending" // pending / done
)
