package com.example.yetimobile.data

import kotlinx.serialization.Serializable
import java.time.Instant

data class UiItem(
    val id: Long? = null,
    val code: String = "",
    val description: String = "",
    val quantity: String = "",
    val batches: String = "",
    val originalQuantity: String = "",
    val originalBatches: String = ""
) {
    fun hasChange(): Boolean = quantity != originalQuantity || batches != originalBatches
}

@Serializable
data class RequestRecord(
    val employee: String,
    val date: String,
    val time: String,
    val timestamp: Long = Instant.now().toEpochMilli(),
    val items: List<RecordItem>
)

@Serializable
data class RecordItem(
    val sector: String,
    val code: String,
    val description: String,
    val quantity: Int,
    val batches: Int
)
