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
data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
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
