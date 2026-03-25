package com.example.yetimobile.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yetimobile.data.HistoryStore
import com.example.yetimobile.data.ItemRepository
import com.example.yetimobile.data.RecordItem
import com.example.yetimobile.data.RequestRecord
import com.example.yetimobile.data.UiItem
import com.example.yetimobile.data.db.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class YetiViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val repo = ItemRepository(db.sectorDao(), db.itemDao())
    private val historyStore = HistoryStore(app)

    private val sectorMap = mapOf(
        "linha-purificador" to "Linha Purificador",
        "pre-montagem" to "Pre-montagem",
        "compressor" to "Compressor",
        "indef" to "Indef"
    )

    private val _currentSector = MutableStateFlow("linha-purificador")
    private val _items = MutableStateFlow<Map<String, List<UiItem>>>(emptyMap())
    private val _loading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _employee = MutableStateFlow("")

    val currentSector: StateFlow<String> = _currentSector
    val itemsBySector: StateFlow<Map<String, List<UiItem>>> = _items
    val loading: StateFlow<Boolean> = _loading
    val error: StateFlow<String?> = _error
    val employee: StateFlow<String> = _employee

    val history: StateFlow<List<RequestRecord>> =
        historyStore.flow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setEmployee(name: String) {
        _employee.value = name
    }

    fun switchSector(sector: String) {
        _currentSector.value = sector
        if (!_items.value.containsKey(sector)) {
            loadSector(sector)
        }
    }

    fun loadInitial() {
        viewModelScope.launch { repo.ensureSectors() }
        loadSector(_currentSector.value)
    }

    fun refreshCurrent() {
        loadSector(_currentSector.value, force = true)
    }

    private fun loadSector(sector: String, force: Boolean = false) {
        viewModelScope.launch {
            if (!force && _items.value.containsKey(sector)) return@launch
            _loading.value = true
            _error.value = null
            runCatching { repo.loadSector(sector) }
                .onSuccess { list ->
                _items.value = _items.value.toMutableMap().apply { put(sector, list) }
            }
                .onFailure { ex ->
                _error.value = ex.message ?: "Erro ao carregar"
            }
            _loading.value = false
        }
    }

    fun updateItemLocal(sector: String, index: Int, item: UiItem) {
        val map = _items.value.toMutableMap()
        val list = map[sector]?.toMutableList() ?: return
        if (index in list.indices) {
            list[index] = item
            map[sector] = list
            _items.value = map
        }
    }

    fun addItem(sector: String, item: UiItem) {
        viewModelScope.launch {
            runCatching { repo.addItem(sector, item) }
                .onSuccess { loadSector(sector, force = true) }
                .onFailure { _error.value = it.message }
        }
    }

    fun saveItem(sector: String, item: UiItem) {
        viewModelScope.launch {
            runCatching { repo.updateItem(sector, item) }
                .onSuccess { loadSector(sector, force = true) }
                .onFailure { _error.value = it.message }
        }
    }

    fun deleteItem(sector: String, id: Long) {
        viewModelScope.launch {
            runCatching { repo.deleteItem(id) }
                .onSuccess { loadSector(sector, force = true) }
                .onFailure { _error.value = it.message }
        }
    }

    fun commitRequest(onPdf: (RequestRecord) -> Unit) {
        val employeeName = employee.value.trim()
        if (employeeName.isEmpty()) {
            _error.value = "Informe o nome do funcionario"
            return
        }
        val changes = mutableListOf<RecordItem>()
        _items.value.forEach { (sectorId, list) ->
            val sectorName = sectorMap[sectorId] ?: sectorId
            list.forEach { item ->
                val q = item.quantity.toIntOrNull() ?: 0
                val b = item.batches.toIntOrNull() ?: 0
                if (q > 0 && b >= 0 && item.hasChange()) {
                    changes.add(
                        RecordItem(
                            sector = sectorName,
                            code = item.code,
                            description = item.description,
                            quantity = q,
                            batches = b
                        )
                    )
                }
            }
        }
        if (changes.isEmpty()) {
            _error.value = "Nenhum item alterado"
            return
        }
        val now = Date()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now)
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
        val record = RequestRecord(
            employee = employeeName,
            date = date,
            time = time,
            items = changes
        )
        viewModelScope.launch {
            val updated = listOf(record) + history.value
            historyStore.save(updated)
            resetOriginals()
            onPdf(record)
        }
    }

    private fun resetOriginals() {
        _items.value = _items.value.mapValues { (_, list) ->
            list.map { it.copy(originalQuantity = it.quantity, originalBatches = it.batches) }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyStore.clear()
        }
    }
}
