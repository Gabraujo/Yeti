package com.example.yetimobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yetimobile.data.UiItem

private val sectorsOrder = listOf("linha-purificador", "pre-montagem", "compressor", "indef")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectorScreen(
    padding: PaddingValues,
    currentSector: String,
    items: List<UiItem>,
    onSelectSector: (String) -> Unit,
    onUpdateItem: (Int, UiItem) -> Unit,
    onSaveItem: (UiItem) -> Unit,
    onAddItem: (UiItem) -> Unit,
    onDeleteItem: (Long) -> Unit,
    sectorName: (String) -> String,
    loading: Boolean,
    error: String?
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(padding)) {
        Row(modifier = Modifier.padding(16.dp)) {
            sectorsOrder.forEach { id ->
                Button(
                    onClick = { onSelectSector(id) },
                    modifier = Modifier.padding(end = 8.dp),
                    enabled = !loading
                ) {
                    Text(sectorName(id))
                }
            }
        }
        LazyColumn {
            itemsIndexed(items) { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = item.code,
                            onValueChange = { onUpdateItem(index, item.copy(code = it)) },
                            label = { Text("Codigo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = item.description,
                            onValueChange = { onUpdateItem(index, item.copy(description = it)) },
                            label = { Text("Descricao") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row {
                            OutlinedTextField(
                                value = item.quantity,
                                onValueChange = { onUpdateItem(index, item.copy(quantity = it)) },
                                label = { Text("Quantidade") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            OutlinedTextField(
                                value = item.batches,
                                onValueChange = { onUpdateItem(index, item.copy(batches = it)) },
                                label = { Text("Lotes") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(onClick = { onSaveItem(item) }, enabled = item.id != null) { Text("Salvar") }
                            Spacer(Modifier.weight(1f))
                            item.id?.let { id ->
                                Button(onClick = { onDeleteItem(id) }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
            item { AddItemCard(onAddItem = onAddItem) }
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun AddItemCard(onAddItem: (UiItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        var newItem by remember { mutableStateOf(UiItem()) }
        Column(Modifier.padding(12.dp)) {
            Text("Adicionar item", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newItem.code,
                onValueChange = { newItem = newItem.copy(code = it) },
                label = { Text("Codigo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newItem.description,
                onValueChange = { newItem = newItem.copy(description = it) },
                label = { Text("Descricao") },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                OutlinedTextField(
                    value = newItem.quantity,
                    onValueChange = { newItem = newItem.copy(quantity = it) },
                    label = { Text("Quantidade") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = newItem.batches,
                    onValueChange = { newItem = newItem.copy(batches = it) },
                    label = { Text("Lotes") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                onAddItem(newItem)
                newItem = UiItem()
            }) { Text("Adicionar") }
        }
    }
}
