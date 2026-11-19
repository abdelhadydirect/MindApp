package com.mind.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mind.app.data.local.MindStorage
import com.mind.app.data.prayer.PrayerEngine
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

    // Active quote displayed on Home screen (null when no quotes exist)
    val activeHomeQuote: StateFlow<QuoteItem?> = combine(
        quotes,
        quoteMode,
        pinnedQuoteId,
        _randomSeed
    ) { list, mode, pinnedId, _ ->
        if (list.isEmpty()) {
            null
        } else {
            when (mode) {
                QuoteDisplayMode.PINNED -> {
                    list.find { it.id == pinnedId } ?: list.first()
                }
                QuoteDisplayMode.RANDOM -> {
                    list.random()
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notes: StateFlow<List<NoteItem>> = storage.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = storage.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Update prayer countdown every 15 seconds (Battery friendly, HH:mm precision)
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
            if (mode == QuoteDisplayMode.RANDOM) {
                _randomSeed.value += 1
            }
        }
    }

    // Notes Actions
    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val newNote = NoteItem(
                title = title.trim().ifBlank { "تدوينة سريعة" },
                content = content.trim()
            )
            storage.saveNotes(listOf(newNote) + notes.value)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            storage.saveNotes(notes.value.filterNot { it.id == id })
        }
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
