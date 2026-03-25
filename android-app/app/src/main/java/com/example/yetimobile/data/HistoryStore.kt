package com.example.yetimobile.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "history")

class HistoryStore(private val context: Context) {
    private val KEY = stringPreferencesKey("history_json")
    private val json = Json { encodeDefaults = true }

    val flow: Flow<List<RequestRecord>> =
        context.dataStore.data.map { prefs ->
            prefs[KEY]?.let {
                runCatching { json.decodeFromString<List<RequestRecord>>(it) }.getOrDefault(emptyList())
            } ?: emptyList()
        }

    suspend fun save(all: List<RequestRecord>) {
        context.dataStore.edit { prefs ->
            prefs[KEY] = json.encodeToString(all)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY)
        }
    }
}
