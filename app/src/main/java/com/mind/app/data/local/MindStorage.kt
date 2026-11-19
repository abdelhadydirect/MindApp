package com.mind.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mind.app.domain.model.NoteItem
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.domain.model.QuoteItem
import com.mind.app.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "mind_storage")

class MindStorage(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val QUOTES_KEY = stringPreferencesKey("quotes_list")
        private val QUOTE_MODE_KEY = stringPreferencesKey("quote_mode")
        private val PINNED_QUOTE_ID_KEY = stringPreferencesKey("pinned_quote_id")
        private val NOTES_KEY = stringPreferencesKey("notes_list")
        private val TASKS_KEY = stringPreferencesKey("tasks_list")
        private val QURAN_VERSE_KEY = stringPreferencesKey("quran_verse")
    }

    val quranVerseFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[QURAN_VERSE_KEY] ?: "وَلَا تَيۡـَٔسُواْ مِن رَّوۡحِ ٱللَّهِ"
    }

    val quotesFlow: Flow<List<QuoteItem>> = context.dataStore.data.map { prefs ->
        prefs[QUOTES_KEY]?.let { runCatching { json.decodeFromString<List<QuoteItem>>(it) }.getOrNull() }
            ?: emptyList()
    }

    val quoteModeFlow: Flow<QuoteDisplayMode> = context.dataStore.data.map { prefs ->
        prefs[QUOTE_MODE_KEY]?.let { runCatching { QuoteDisplayMode.valueOf(it) }.getOrNull() }
            ?: QuoteDisplayMode.PINNED
    }

    val pinnedQuoteIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[PINNED_QUOTE_ID_KEY]
    }

    val notesFlow: Flow<List<NoteItem>> = context.dataStore.data.map { prefs ->
        prefs[NOTES_KEY]?.let { runCatching { json.decodeFromString<List<NoteItem>>(it) }.getOrNull() }
            ?: emptyList()
    }

    val tasksFlow: Flow<List<TaskItem>> = context.dataStore.data.map { prefs ->
        prefs[TASKS_KEY]?.let { runCatching { json.decodeFromString<List<TaskItem>>(it) }.getOrNull() }
            ?: emptyList()
    }

    suspend fun saveQuranVerse(verse: String) {
        context.dataStore.edit { it[QURAN_VERSE_KEY] = verse }
    }

    suspend fun saveQuotes(quotes: List<QuoteItem>) {
        context.dataStore.edit { it[QUOTES_KEY] = json.encodeToString(quotes) }
    }

    suspend fun setQuoteMode(mode: QuoteDisplayMode) {
        context.dataStore.edit { it[QUOTE_MODE_KEY] = mode.name }
    }

    suspend fun setPinnedQuoteId(id: String?) {
        context.dataStore.edit { prefs ->
            if (id == null) prefs.remove(PINNED_QUOTE_ID_KEY)
            else prefs[PINNED_QUOTE_ID_KEY] = id
        }
    }

    suspend fun saveNotes(notes: List<NoteItem>) {
        context.dataStore.edit { it[NOTES_KEY] = json.encodeToString(notes) }
    }

    suspend fun saveTasks(tasks: List<TaskItem>) {
        context.dataStore.edit { it[TASKS_KEY] = json.encodeToString(tasks) }
    }
}
