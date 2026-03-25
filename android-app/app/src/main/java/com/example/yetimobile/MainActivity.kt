package com.example.yetimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yetimobile.pdf.PdfExporter
import com.example.yetimobile.ui.YetiViewModel
import com.example.yetimobile.ui.screens.HistoryScreen
import com.example.yetimobile.ui.screens.HomeScreen
import com.example.yetimobile.ui.screens.SectorScreen
import com.example.yetimobile.ui.theme.YetiTheme

class MainActivity : ComponentActivity() {

    private val vm by viewModels<YetiViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.loadInitial()
        setContent {
            YetiTheme {
                val current by vm.currentSector.collectAsStateWithLifecycle()
                val items by vm.itemsBySector.collectAsStateWithLifecycle()
                val history by vm.history.collectAsStateWithLifecycle()
                val loading by vm.loading.collectAsStateWithLifecycle()
                val error by vm.error.collectAsStateWithLifecycle()
                val employee by vm.employee.collectAsStateWithLifecycle()

                var tab by remember { mutableStateOf(Tab.Home) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = tab == Tab.Home,
                                onClick = { tab = Tab.Home },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Requisicao") },
                                label = { Text("Requisicao") }
                            )
                            NavigationBarItem(
                                selected = tab == Tab.Sectors,
                                onClick = { tab = Tab.Sectors },
                                icon = { Icon(Icons.Default.List, contentDescription = "Setores") },
                                label = { Text("Setores") }
                            )
                            NavigationBarItem(
                                selected = tab == Tab.History,
                                onClick = { tab = Tab.History },
                                icon = { Icon(Icons.Default.History, contentDescription = "Historico") },
                                label = { Text("Historico") }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    when (tab) {
                        Tab.Home -> HomeScreen(
                            padding = padding,
                            employee = employee,
                            onEmployeeChange = vm::setEmployee,
                            currentSector = current,
                            sectorName = sectorName(current),
                            items = items[current].orEmpty(),
                            loading = loading,
                            error = error,
                            onRefresh = vm::refreshCurrent,
                            onCommit = {
                                vm.commitRequest { record ->
                                    PdfExporter.export(
                                        context = this@MainActivity,
                                        employee = record.employee,
                                        date = record.date,
                                        time = record.time,
                                        items = record.items
                                    )
                                }
                            }
                        )
                        Tab.Sectors -> SectorScreen(
                            padding = padding,
                            currentSector = current,
                            items = items[current].orEmpty(),
                            onSelectSector = { vm.switchSector(it) },
                            onUpdateItem = { idx, item -> vm.updateItemLocal(current, idx, item) },
                            onSaveItem = { vm.saveItem(current, it) },
                            onAddItem = { vm.addItem(current, it) },
                            onDeleteItem = { id -> vm.deleteItem(current, id) },
                            sectorName = ::sectorName,
                            loading = loading,
                            error = error
                        )
                        Tab.History -> HistoryScreen(
                            padding = padding,
                            history = history,
                            onClear = vm::clearHistory,
                            onExport = { record ->
                                PdfExporter.export(
                                    context = this@MainActivity,
                                    employee = record.employee,
                                    date = record.date,
                                    time = record.time,
                                    items = record.items
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun sectorName(id: String): String = when (id) {
        "linha-purificador" -> "Linha Purificador"
        "pre-montagem" -> "Pre-montagem"
        "compressor" -> "Compressor"
        "indef" -> "Indef"
        else -> id
    }
}

private enum class Tab { Home, Sectors, History }
