package com.example.yetimobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yetimobile.data.UiItem

@Composable
fun HomeScreen(
    padding: PaddingValues,
    employee: String,
    onEmployeeChange: (String) -> Unit,
    currentSector: String,
    sectorName: String,
    items: List<UiItem>,
    loading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onCommit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding
    ) {
        item {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Requisicao de Material", style = MaterialTheme.typography.titleLarge)
                    Text("Setor atual: $sectorName", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = employee,
                        onValueChange = onEmployeeChange,
                        label = { Text("Funcionario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onCommit, enabled = !loading) {
                        Text("Requisitar")
                    }
                    if (loading) {
                        Spacer(Modifier.height(12.dp))
                        CircularProgressIndicator()
                    }
                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(item.code, style = MaterialTheme.typography.titleMedium)
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                    Text("Quantidade: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                    Text("Lotes: ${item.batches}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
