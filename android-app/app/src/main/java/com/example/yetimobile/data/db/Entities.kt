package com.example.yetimobile.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectors")
data class SectorEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectorId: String,
    val code: String,
    val description: String,
    val quantityPerBatch: Int,
    val batches: Int
)
