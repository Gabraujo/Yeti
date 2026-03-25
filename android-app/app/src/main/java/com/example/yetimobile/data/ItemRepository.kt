package com.example.yetimobile.data

import com.example.yetimobile.data.db.ItemDao
import com.example.yetimobile.data.db.ItemEntity
import com.example.yetimobile.data.db.SectorDao
import com.example.yetimobile.data.db.SectorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(
    private val sectorDao: SectorDao,
    private val itemDao: ItemDao
) {

    private val defaultSectors = listOf(
        SectorEntity("linha-purificador", "Linha Purificador"),
        SectorEntity("pre-montagem", "Pre-montagem"),
        SectorEntity("compressor", "Compressor"),
        SectorEntity("indef", "Indef")
    )

    suspend fun ensureSectors() = withContext(Dispatchers.IO) {
        if (sectorDao.all().isEmpty()) {
            sectorDao.upsertAll(defaultSectors)
        }
    }

    suspend fun loadSector(sector: String): List<UiItem> = withContext(Dispatchers.IO) {
        itemDao.listBySector(sector).map { it.toUi() }
    }

    suspend fun addItem(sector: String, item: UiItem) = withContext(Dispatchers.IO) {
        itemDao.insert(
            ItemEntity(
                sectorId = sector,
                code = item.code,
                description = item.description,
                quantityPerBatch = item.quantity.toIntOrNull() ?: 0,
                batches = item.batches.toIntOrNull() ?: 0
            )
        )
    }

    suspend fun updateItem(sector: String, item: UiItem) = withContext(Dispatchers.IO) {
        val id = item.id ?: return@withContext
        itemDao.update(
            ItemEntity(
                id = id,
                sectorId = sector,
                code = item.code,
                description = item.description,
                quantityPerBatch = item.quantity.toIntOrNull() ?: 0,
                batches = item.batches.toIntOrNull() ?: 0
            )
        )
    }

    suspend fun deleteItem(id: Long) = withContext(Dispatchers.IO) {
        itemDao.delete(id)
    }

    private fun ItemEntity.toUi() = UiItem(
        id = id,
        code = code,
        description = description,
        quantity = quantityPerBatch.toString(),
        batches = batches.toString(),
        originalQuantity = quantityPerBatch.toString(),
        originalBatches = batches.toString()
    )
}
