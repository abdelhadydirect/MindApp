package com.mind.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mind.app.data.local.MindStorage
import com.mind.app.data.prayer.PrayerEngine
import com.mind.app.domain.model.AudioTrackItem
import com.mind.app.domain.model.NoteFolder
import com.mind.app.domain.model.NoteItem
import com.mind.app.domain.model.PrayerDashboardState
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.domain.model.QuoteItem
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

    private val storage = MindStorage(application)

    private val _prayerState = MutableStateFlow(PrayerEngine.calculateDashboardState())
    val prayerState: StateFlow<PrayerDashboardState> = _prayerState.asStateFlow()

    val quranVerse: StateFlow<String> = storage.quranVerseFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "وَلَا تَيۡـَٔسُواْ مِن رَّوۡحِ ٱللَّهِ")

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

    // Folders & Notes
    val folders: StateFlow<List<NoteFolder>> = storage.foldersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteItem>> = storage.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = storage.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            while (true) {
                _prayerState.value = PrayerEngine.calculateDashboardState()
                delay(15_000L)
            }
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

    // Folders Actions (No default folder - user creates manually)
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
            // Keep notes safe by detaching them from the deleted folder
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

    // Tasks Actions
    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newTask = TaskItem(title = title.trim())
            storage.saveTasks(listOf(newTask) + tasks.value)
        }
    }

    fun toggleTask(id: String) {
        viewModelScope.launch {
            val updated = tasks.value.map {
                if (it.id == id) it.copy(isDone = !it.isDone) else it
            }
            storage.saveTasks(updated)
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            storage.saveTasks(tasks.value.filterNot { it.id == id })
        }
    }
}
