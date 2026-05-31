package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val developer: String = "",
    val location: String = "",
    val delivery: String = "",
    val priceFrom: Double = 0.0,
    val priceTo: Double = 0.0,
    val down: Double = 0.0,
    val years: Int = 0,
    val comm: Double = 0.0,
    val status: String = "متاح",
    val desc: String = ""
)
