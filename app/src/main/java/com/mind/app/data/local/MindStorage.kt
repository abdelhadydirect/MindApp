package com.mind.app.data.local

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mind.app.domain.model.NoteFolder
import com.mind.app.domain.model.NoteItem
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.domain.model.QuoteItem
import com.mind.app.domain.model.QuranBookmark
import com.mind.app.domain.model.QuranSurah
import com.mind.app.domain.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "mind_storage")

class MindStorage(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val APP_LIGHT_THEME_KEY = booleanPreferencesKey("app_light_theme")
        private val QUOTES_KEY = stringPreferencesKey("quotes_list")
        private val QUOTE_MODE_KEY = stringPreferencesKey("quote_mode")
        private val PINNED_QUOTE_ID_KEY = stringPreferencesKey("pinned_quote_id")
        private val FOLDERS_KEY = stringPreferencesKey("note_folders_list")
        private val NOTES_KEY = stringPreferencesKey("notes_list_v2")
        private val TASKS_KEY = stringPreferencesKey("tasks_list_v2")
        private val QURAN_VERSE_KEY = stringPreferencesKey("quran_verse")
        private val QURAN_BOOKMARK_KEY = stringPreferencesKey("quran_bookmark")
        private val QURAN_FONT_SIZE_KEY = floatPreferencesKey("quran_font_size")
        private val QURAN_SEPIA_MODE_KEY = booleanPreferencesKey("quran_sepia_mode")
        private val ADHAN_TONE_URI_KEY = stringPreferencesKey("adhan_tone_uri")
        private val ADHAN_TONE_NAME_KEY = stringPreferencesKey("adhan_tone_name")
        private val ADHAN_ENABLED_KEY = booleanPreferencesKey("adhan_enabled")
        private val TASK_TONE_URI_KEY = stringPreferencesKey("task_tone_uri")
        private val TASK_TONE_NAME_KEY = stringPreferencesKey("task_tone_name")
    }

    val appLightThemeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[APP_LIGHT_THEME_KEY] ?: false
    }

    val quranVerseFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[QURAN_VERSE_KEY] ?: "وَلَا تَيۡـَٔسُواْ مِن رَّوۡحِ ٱللَّهِ"
    }

    val quranBookmarkFlow: Flow<QuranBookmark> = context.dataStore.data.map { prefs ->
        prefs[QURAN_BOOKMARK_KEY]?.let { runCatching { json.decodeFromString<QuranBookmark>(it) }.getOrNull() }
            ?: QuranBookmark()
    }

    val quranFontSizeFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[QURAN_FONT_SIZE_KEY] ?: 25f
    }

    val quranSepiaModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[QURAN_SEPIA_MODE_KEY] ?: false
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

    val foldersFlow: Flow<List<NoteFolder>> = context.dataStore.data.map { prefs ->
        prefs[FOLDERS_KEY]?.let { runCatching { json.decodeFromString<List<NoteFolder>>(it) }.getOrNull() }
            ?: emptyList()
    }

    val notesFlow: Flow<List<NoteItem>> = context.dataStore.data.map { prefs ->
        prefs[NOTES_KEY]?.let { runCatching { json.decodeFromString<List<NoteItem>>(it) }.getOrNull() }
            ?: emptyList()
    }

    val tasksFlow: Flow<List<TaskItem>> = context.dataStore.data.map { prefs ->
        prefs[TASKS_KEY]?.let { runCatching { json.decodeFromString<List<TaskItem>>(it) }.getOrNull() }
            ?: emptyList()
    }

    val adhanEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ADHAN_ENABLED_KEY] ?: true
    }

    val adhanToneUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ADHAN_TONE_URI_KEY]
    }

    val adhanToneNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ADHAN_TONE_NAME_KEY] ?: "نغمة التنبيه الافتراضية للهاتف"
    }

    val taskToneUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TASK_TONE_URI_KEY]
    }

    val taskToneNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TASK_TONE_NAME_KEY] ?: "نغمة المنبه الافتراضية للهاتف"
    }

    suspend fun getAdhanToneUriSync(): String? = adhanToneUriFlow.first()
    suspend fun getTaskToneUriSync(): String? = taskToneUriFlow.first()
    suspend fun isAdhanEnabledSync(): Boolean = adhanEnabledFlow.first()
    suspend fun getTasksSync(): List<TaskItem> = tasksFlow.first()

    suspend fun saveAppLightTheme(isLight: Boolean) {
        context.dataStore.edit {
            it[APP_LIGHT_THEME_KEY] = isLight
            it[QURAN_SEPIA_MODE_KEY] = isLight
        }
    }

    suspend fun saveQuranVerse(verse: String) {
        context.dataStore.edit { it[QURAN_VERSE_KEY] = verse }
    }

    suspend fun saveQuranBookmark(bookmark: QuranBookmark) {
        context.dataStore.edit { it[QURAN_BOOKMARK_KEY] = json.encodeToString(bookmark) }
    }

    suspend fun saveQuranFontSize(size: Float) {
        context.dataStore.edit { it[QURAN_FONT_SIZE_KEY] = size }
    }

    suspend fun saveQuranSepiaMode(isSepia: Boolean) {
        context.dataStore.edit { it[QURAN_SEPIA_MODE_KEY] = isSepia }
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

    suspend fun saveFolders(folders: List<NoteFolder>) {
        context.dataStore.edit { it[FOLDERS_KEY] = json.encodeToString(folders) }
    }

    suspend fun saveNotes(notes: List<NoteItem>) {
        context.dataStore.edit { it[NOTES_KEY] = json.encodeToString(notes) }
    }

    suspend fun saveTasks(tasks: List<TaskItem>) {
        context.dataStore.edit { it[TASKS_KEY] = json.encodeToString(tasks) }
    }

    suspend fun saveAdhanSettings(enabled: Boolean, uri: String?, name: String) {
        context.dataStore.edit { prefs ->
            prefs[ADHAN_ENABLED_KEY] = enabled
            if (uri != null) prefs[ADHAN_TONE_URI_KEY] = uri
            prefs[ADHAN_TONE_NAME_KEY] = name
        }
    }

    suspend fun setAdhanEnabled(enabled: Boolean) {
        context.dataStore.edit { it[ADHAN_ENABLED_KEY] = enabled }
    }

    suspend fun saveTaskTone(uri: String?, name: String) {
        context.dataStore.edit { prefs ->
            if (uri != null) prefs[TASK_TONE_URI_KEY] = uri
            prefs[TASK_TONE_NAME_KEY] = name
        }
    }

    suspend fun loadFullQuranOffline(): List<QuranSurah> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = context.assets.open("quran.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<QuranSurah>>(raw)
        }.getOrDefault(emptyList())
    }

    suspend fun importMediaUri(uri: Uri, prefix: String): Pair<String, String>? = withContext(Dispatchers.IO) {
        runCatching {
            val mediaDir = File(context.filesDir, "mind_media").apply { mkdirs() }
            val originalName = resolveDisplayName(uri) ?: "${prefix}_${System.currentTimeMillis()}"
            val safeExt = originalName.substringAfterLast('.', "").takeIf { it.isNotBlank() }?.let { ".$it" } ?: ""
            val destFile = File(mediaDir, "${prefix}_${UUID.randomUUID()}$safeExt")

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val cleanTitle = originalName.substringBeforeLast('.')
            Pair(cleanTitle, destFile.absolutePath)
        }.getOrNull()
    }

    private fun resolveDisplayName(uri: Uri): String? {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx != -1 && cursor.moveToFirst()) {
                    return cursor.getString(idx)
                }
            }
        }
        return uri.lastPathSegment
    }
}
