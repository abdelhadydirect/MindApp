package com.mind.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mind.app.alarm.MindAlarmScheduler
import com.mind.app.data.local.MindStorage
import com.mind.app.data.prayer.PrayerEngine
import com.mind.app.domain.model.AudioTrackItem
import com.mind.app.domain.model.NoteFolder
import com.mind.app.domain.model.NoteItem
import com.mind.app.domain.model.PrayerDashboardState
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.domain.model.QuoteItem
import com.mind.app.domain.model.QuranBookmark
import com.mind.app.domain.model.QuranSurah
import com.mind.app.domain.model.TaskItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MindViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val storage = MindStorage(application)

    private val _prayerState = MutableStateFlow(PrayerEngine.calculateDashboardState())
    val prayerState: StateFlow<PrayerDashboardState> = _prayerState.asStateFlow()

    val ambientMusicEnabled = MutableStateFlow(true)
    fun toggleAmbientMusic() { ambientMusicEnabled.value = !ambientMusicEnabled.value }
    val appLightTheme: StateFlow<Boolean> = storage.appLightThemeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val quranVerse: StateFlow<String> = storage.quranVerseFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "وَلَا تَيۡـَٔسُواْ مِن رَّوۡحِ ٱللَّهِ")

    private val _quranSurahs = MutableStateFlow<List<QuranSurah>>(emptyList())
    val quranSurahs: StateFlow<List<QuranSurah>> = _quranSurahs.asStateFlow()

    val quranBookmark: StateFlow<QuranBookmark> = storage.quranBookmarkFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuranBookmark())

    val quranFontSize: StateFlow<Float> = storage.quranFontSizeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25f)

    val quranSepiaMode: StateFlow<Boolean> = storage.quranSepiaModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val quotes: StateFlow<List<QuoteItem>> = storage.quotesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quoteMode: StateFlow<QuoteDisplayMode> = storage.quoteModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuoteDisplayMode.PINNED)

    val pinnedQuoteId: StateFlow<String?> = storage.pinnedQuoteIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _randomSeed = MutableStateFlow(0)

    val activeHomeQuote: StateFlow<QuoteItem?> = combine(
        quotes,
        quoteMode,
        pinnedQuoteId,
        _randomSeed
    ) { list, mode, pinnedId, _ ->
        if (list.isEmpty()) null
        else when (mode) {
            QuoteDisplayMode.PINNED -> list.find { it.id == pinnedId } ?: list.first()
            QuoteDisplayMode.RANDOM -> list.random()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val folders: StateFlow<List<NoteFolder>> = storage.foldersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteItem>> = storage.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = storage.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adhanEnabled: StateFlow<Boolean> = storage.adhanEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val adhanToneUri: StateFlow<String?> = storage.adhanToneUriFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adhanToneName: StateFlow<String> = storage.adhanToneNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "نغمة التنبيه الافتراضية للهاتف")

    val taskToneUri: StateFlow<String?> = storage.taskToneUriFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val taskToneName: StateFlow<String> = storage.taskToneNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "نغمة المنبه الافتراضية للهاتف")

    init {
        viewModelScope.launch {
            _quranSurahs.value = storage.loadFullQuranOffline()
            if (storage.isAdhanEnabledSync()) {
                MindAlarmScheduler.scheduleNextAdhan(appContext)
            }
            while (true) {
                _prayerState.value = PrayerEngine.calculateDashboardState()
                delay(15_000L)
            }
        }
    }

    // Quran Reader Actions
    fun toggleAppLightTheme(isLight: Boolean) { viewModelScope.launch { storage.saveAppLightTheme(isLight) } }
    fun saveBookmark(surahId: Int, surahName: String, verseId: Int) {
        viewModelScope.launch {
            storage.saveQuranBookmark(QuranBookmark(surahId, surahName, verseId))
        }
    }

    fun updateQuranFontSize(newSize: Float) {
        viewModelScope.launch {
            storage.saveQuranFontSize(newSize.coerceIn(18f, 38f))
        }
    }

    fun toggleQuranSepiaMode(isSepia: Boolean) {
        viewModelScope.launch {
            storage.saveQuranSepiaMode(isSepia)
        }
    }

    fun shuffleRandomQuote() {
        if (quoteMode.value == QuoteDisplayMode.RANDOM && quotes.value.size > 1) {
            _randomSeed.value += 1
        }
    }

    fun updateQuranVerse(newVerse: String) {
        if (newVerse.isNotBlank()) {
            viewModelScope.launch { storage.saveQuranVerse(newVerse.trim()) }
        }
    }

    // Adhan & Task Ringtone Settings
    fun toggleAdhanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            storage.setAdhanEnabled(enabled)
            if (enabled) MindAlarmScheduler.scheduleNextAdhan(appContext)
            else MindAlarmScheduler.cancelAdhan(appContext)
        }
    }

    fun updateAdhanTone(uri: String?, name: String) {
        viewModelScope.launch {
            storage.saveAdhanSettings(adhanEnabled.value, uri, name)
            if (adhanEnabled.value) MindAlarmScheduler.scheduleNextAdhan(appContext)
        }
    }

    fun updateTaskTone(uri: String?, name: String) {
        viewModelScope.launch {
            storage.saveTaskTone(uri, name)
        }
    }

    // Quotes Actions
    fun addQuote(text: String, pinImmediately: Boolean = true) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val newItem = QuoteItem(text = text.trim())
            val updated = listOf(newItem) + quotes.value
            storage.saveQuotes(updated)
            if (pinImmediately || pinnedQuoteId.value == null) {
                storage.setPinnedQuoteId(newItem.id)
                storage.setQuoteMode(QuoteDisplayMode.PINNED)
            }
        }
    }

    fun deleteQuote(id: String) {
        viewModelScope.launch {
            val updated = quotes.value.filterNot { it.id == id }
            storage.saveQuotes(updated)
            if (pinnedQuoteId.value == id) {
                storage.setPinnedQuoteId(updated.firstOrNull()?.id)
            }
        }
    }

    fun pinQuote(id: String) {
        viewModelScope.launch {
            storage.setPinnedQuoteId(id)
            storage.setQuoteMode(QuoteDisplayMode.PINNED)
        }
    }

    fun setQuoteDisplayMode(mode: QuoteDisplayMode) {
        viewModelScope.launch {
            storage.setQuoteMode(mode)
            if (mode == QuoteDisplayMode.RANDOM) _randomSeed.value += 1
        }
    }

    // Folders Actions
    fun createFolder(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newFolder = NoteFolder(name = name.trim())
            storage.saveFolders(folders.value + newFolder)
        }
    }

    fun renameFolder(folderId: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updated = folders.value.map {
                if (it.id == folderId) it.copy(name = newName.trim()) else it
            }
            storage.saveFolders(updated)
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            storage.saveFolders(folders.value.filterNot { it.id == folderId })
            val updatedNotes = notes.value.map {
                if (it.folderId == folderId) it.copy(folderId = null) else it
            }
            storage.saveNotes(updatedNotes)
        }
    }

    // Rich Notes Actions
    fun upsertNote(note: NoteItem) {
        viewModelScope.launch {
            val existingIdx = notes.value.indexOfFirst { it.id == note.id }
            val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
            val updatedList = if (existingIdx != -1) {
                notes.value.toMutableList().apply { set(existingIdx, updatedNote) }
            } else {
                listOf(updatedNote) + notes.value
            }
            storage.saveNotes(updatedList)
        }
    }

    fun moveNoteToFolder(noteId: String, targetFolderId: String?) {
        viewModelScope.launch {
            val updated = notes.value.map {
                if (it.id == noteId) it.copy(folderId = targetFolderId, updatedAt = System.currentTimeMillis()) else it
            }
            storage.saveNotes(updated)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            storage.saveNotes(notes.value.filterNot { it.id == id })
        }
    }

    suspend fun importImage(uri: Uri): String? = storage.importMediaUri(uri, "img")?.second

    suspend fun importVideo(uri: Uri): String? = storage.importMediaUri(uri, "vid")?.second

    suspend fun importAudio(uri: Uri): AudioTrackItem? {
        val imported = storage.importMediaUri(uri, "aud") ?: return null
        return AudioTrackItem(title = imported.first, filePath = imported.second)
    }

    // Multi-Day & Scheduled Tasks Actions
    fun addBatchTasks(newTasks: List<TaskItem>) {
        val valid = newTasks.filter { it.title.isNotBlank() }
        if (valid.isEmpty()) return
        viewModelScope.launch {
            storage.saveTasks(valid + tasks.value)
            valid.forEach { task ->
                if (task.dueAtMillis != null) {
                    MindAlarmScheduler.scheduleTaskAlarm(appContext, task)
                }
            }
        }
    }

    fun toggleTask(id: String) {
        viewModelScope.launch {
            val updated = tasks.value.map {
                if (it.id == id) {
                    val toggled = it.copy(isDone = !it.isDone)
                    if (toggled.isDone) MindAlarmScheduler.cancelTaskAlarm(appContext, toggled.id)
                    else MindAlarmScheduler.scheduleTaskAlarm(appContext, toggled)
                    toggled
                } else it
            }
            storage.saveTasks(updated)
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            MindAlarmScheduler.cancelTaskAlarm(appContext, id)
            storage.saveTasks(tasks.value.filterNot { it.id == id })
        }
    }
}
