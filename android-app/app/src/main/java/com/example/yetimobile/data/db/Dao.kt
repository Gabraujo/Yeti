package com.example.yetimobile.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SectorDao {
    @Query("SELECT * FROM sectors")
    suspend fun all(): List<SectorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(sectors: List<SectorEntity>)
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE sectorId = :sector ORDER BY id")
    suspend fun listBySector(sector: String): List<ItemEntity>

    @Insert
    suspend fun insert(item: ItemEntity)

    @Update
    suspend fun update(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun delete(id: Long)
}
