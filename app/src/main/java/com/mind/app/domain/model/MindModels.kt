package com.mind.app.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class QuoteItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
enum class QuoteDisplayMode {
    PINNED,
    RANDOM
}

@Serializable
data class NoteFolder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class AudioTrackItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val filePath: String
)

@Serializable
data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val folderId: String? = null,
    val title: String = "",
    val content: String = "",
    val imagePaths: List<String> = emptyList(),
    val videoPath: String? = null,
    val audioPlaylist: List<AudioTrackItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val imagePath: String? = null,
    val dueAtMillis: Long? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class QuranVerseItem(
    val id: Int,
    val text: String
)

@Serializable
data class QuranSurah(
    val id: Int,
    val name: String,
    val transliteration: String = "",
    val type: String = "",
    val total_verses: Int = 0,
    val verses: List<QuranVerseItem> = emptyList()
)

@Serializable
data class QuranBookmark(
    val surahId: Int = 1,
    val surahName: String = "الفاتحة",
    val verseId: Int = 1
)

data class SinglePrayer(
    val nameAr: String,
    val timeFormatted: String,
    val epochMillis: Long,
    val isNext: Boolean = false
)

data class PrayerDashboardState(
    val dayNameAr: String = "",
    val hijriDateAr: String = "",
    val gregorianDateAr: String = "",
    val nextPrayerName: String = "",
    val nextPrayerTimeFormatted: String = "",
    val remainingHoursMinutes: String = "00:00",
    val elapsedText: String = "",
    val remainingText: String = "",
    val progress: Float = 0f,
    val prayersList: List<SinglePrayer> = emptyList()
)
