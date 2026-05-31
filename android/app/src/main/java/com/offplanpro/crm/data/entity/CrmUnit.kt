package com.offplanpro.crm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class CrmUnit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String = "",
    val project: String = "",
    val type: String = "Apartment", // Apartment / Duplex / Penthouse / Villa / Townhouse / Office
    val area: Double = 0.0,
    val rooms: Int = 0,
    val price: Double = 0.0,
    val status: String = "Available", // Available / Reserved / Sold
    val desc: String = ""
)
