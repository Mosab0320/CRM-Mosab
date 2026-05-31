package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String = "",
    val color: String = "gold",
    val time: String = ""
)
