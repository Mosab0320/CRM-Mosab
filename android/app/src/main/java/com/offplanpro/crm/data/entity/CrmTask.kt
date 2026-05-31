package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class CrmTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val type: String = "مكالمة", // مكالمة / معاينة / اجتماع / متابعة / أوراق وعقود / تسجيل
    val priority: String = "عادية", // عادية / مهمة / عاجلة
    val date: String = "",
    val client: String = "",
    val project: String = "",
    val notes: String = "",
    val done: Boolean = false
)
