package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey
    val id: Int = 1,
    val dealsM: Int = 0,
    val commM: Double = 0.0,
    val leadsM: Int = 0,
    val visitsM: Int = 0,
    val followupsM: Int = 0,
    val dealsY: Int = 0,
    val commY: Double = 0.0,
    val brokerName: String = ""
)
