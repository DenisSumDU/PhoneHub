package com.example.phonehub

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phones")
data class PhoneEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val price: Double,
    val ramGb: Int,
    val storageGb: Int,
    val batteryCapacity: Int,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
