package com.example.yetimobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yetimobile.data.RequestRecord

@Composable
fun HistoryScreen(
    padding: PaddingValues,
    history: List<RequestRecord>,
    onClear: () -> Unit,
    onExport: (RequestRecord) -> Unit
) {
    var dialog by remember { mutableStateOf<RequestRecord?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Button(
            onClick = onClear,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            enabled = history.isNotEmpty()
        ) {
            Text("Limpar historico")
        }
        LazyColumn {
            itemsIndexed(history) { _, rec ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { dialog = rec }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(rec.employee)
                        Text("${rec.date} ${rec.time}")
                        Text("Itens: ${rec.items.size}")
                        Button(onClick = { onExport(rec) }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("PDF")
                        }
                    }
                }
            }
        }
    }
    dialog?.let { rec ->
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = {
                TextButton(onClick = { dialog = null }) { Text("Fechar") }
            },
            title = { Text("Itens da requisicao") },
            text = {
                Column {
                    rec.items.forEach {
                        Text("${it.sector} - ${it.code} - ${it.description} (${it.quantity}) Lotes ${it.batches}")
                    }
                }
            }
        )
    }
}
