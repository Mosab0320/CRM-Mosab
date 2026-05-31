package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val type: String = "مشتري", // مشتري / مستثمر / بائع
    val heat: String = "warm", // hot / warm / cold
    val stage: String = "ليد جديد", // ليد جديد / تم التواصل / معاينة / عرض سعر / تفاوض / تم الإغلاق
    val budget: Double = 0.0,
    val project: String = "",
    val unitType: String = "",
    val source: String = "",
    val nationality: String = "",
    val lastContact: String = "",
    val notes: String = "",
    val date: String = ""
)
