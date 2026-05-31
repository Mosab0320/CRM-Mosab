package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deals")
data class Deal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val client: String = "",
    val project: String = "",
    val unit: String = "",
    val type: String = "",
    val value: Double = 0.0,
    val commPct: Double = 0.0,
    val commTotal: Double = 0.0,
    val myPct: Double = 0.0,
    val myComm: Double = 0.0,
    val date: String = "",
    val collectDate: String = "",
    val status: String = "جارية", // مكتملة / جارية / ملغاة
    val contractStage: String = "",
    val notes: String = ""
)
