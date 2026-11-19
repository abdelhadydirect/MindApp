package com.mind.app.data.prayer

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Prayer
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.mind.app.domain.model.PrayerDashboardState
import com.mind.app.domain.model.SinglePrayer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object PrayerEngine {

    private val arabicLocale = Locale("ar", "EG")

    // Default coordinates: Cairo, Egypt (can be overridden by device GPS)
    fun calculateDashboardState(
        latitude: Double = 30.0444,
        longitude: Double = 31.2357,
        nowMillis: Long = System.currentTimeMillis()
    ): PrayerDashboardState {
        val zoneId = ZoneId.systemDefault()
        val nowZoned = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
        val localDate = nowZoned.toLocalDate()

        // 1. Arabic Day Name
        val dayName = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, arabicLocale)

        // 2. Hijri Date
        val hijriDate = HijrahDate.from(localDate)
        val hijriFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'هـ'", arabicLocale)
        val hijriFormatted = convertToArabicDigits(hijriFormatter.format(hijriDate))

        // 3. Gregorian Date
        val gregorianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", arabicLocale)
        val gregorianFormatted = convertToArabicDigits(gregorianFormatter.format(localDate))

        // 4. Prayer Times Calculation (Egyptian General Authority of Survey)
        val coordinates = Coordinates(latitude, longitude)
        val params = CalculationMethod.EGYPTIAN.parameters
        val todayComponents = DateComponents(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        val todayPrayers = PrayerTimes(coordinates, todayComponents, params)

        val tomorrowDate = localDate.plusDays(1)
        val tomorrowComponents = DateComponents(tomorrowDate.year, tomorrowDate.monthValue, tomorrowDate.dayOfMonth)
        val tomorrowPrayers = PrayerTimes(coordinates, tomorrowComponents, params)

        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", arabicLocale)

        data class RawPrayer(val prayer: Prayer, val nameAr: String, val millis: Long)

        val todayList = listOf(
            RawPrayer(Prayer.FAJR, "الفجر", todayPrayers.fajr.toEpochMilliseconds()),
            RawPrayer(Prayer.SUNRISE, "الشروق", todayPrayers.sunrise.toEpochMilliseconds()),
            RawPrayer(Prayer.DHUHR, "الظهر", todayPrayers.dhuhr.toEpochMilliseconds()),
            RawPrayer(Prayer.ASR, "العصر", todayPrayers.asr.toEpochMilliseconds()),
            RawPrayer(Prayer.MAGHRIB, "المغرب", todayPrayers.maghrib.toEpochMilliseconds()),
            RawPrayer(Prayer.ISHA, "العشاء", todayPrayers.isha.toEpochMilliseconds())
        )

        val nextIdx = todayList.indexOfFirst { it.millis > nowMillis }

        val nextPrayerName: String
        val nextPrayerMillis: Long
        val prevPrayerMillis: Long

        if (nextIdx != -1) {
            nextPrayerName = todayList[nextIdx].nameAr
            nextPrayerMillis = todayList[nextIdx].millis
            prevPrayerMillis = if (nextIdx > 0) {
                todayList[nextIdx - 1].millis
            } else {
                val yesterdayDate = localDate.minusDays(1)
                val yesterdayComp = DateComponents(yesterdayDate.year, yesterdayDate.monthValue, yesterdayDate.dayOfMonth)
                PrayerTimes(coordinates, yesterdayComp, params).isha.toEpochMilliseconds()
            }
        } else {
            // After Isha -> Next is tomorrow's Fajr
            nextPrayerName = "الفجر"
            nextPrayerMillis = tomorrowPrayers.fajr.toEpochMilliseconds()
            prevPrayerMillis = todayList.last().millis
        }

        // Format Next Prayer Time
        val nextPrayerZoned = Instant.ofEpochMilli(nextPrayerMillis).atZone(zoneId)
        val nextTimeStr = formatArabicAmPm(timeFormatter.format(nextPrayerZoned))

        // Remaining & Elapsed Calculations (Hours & Minutes ONLY)
        val remainingMillis = (nextPrayerMillis - nowMillis).coerceAtLeast(0L)
        val elapsedMillis = (nowMillis - prevPrayerMillis).coerceAtLeast(0L)
        val totalSpan = (nextPrayerMillis - prevPrayerMillis).coerceAtLeast(1L)
        val progress = (elapsedMillis.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)

        val remTotalMinutes = (remainingMillis / 60_000L).toInt()
        val remHours = remTotalMinutes / 60
        val remMins = remTotalMinutes % 60
        val remainingHHMM = String.format(Locale.US, "%02d:%02d", remHours, remMins)

        val elapTotalMinutes = (elapsedMillis / 60_000L).toInt()
        val elapHours = elapTotalMinutes / 60
        val elapMins = elapTotalMinutes % 60

        val elapsedText = "مضى ${elapHours}س ${elapMins}د"
        val remainingText = "متبقٍ ${remHours}س ${remMins}د"

        val singlePrayers = todayList.mapIndexed { index, raw ->
            val zoned = Instant.ofEpochMilli(raw.millis).atZone(zoneId)
            SinglePrayer(
                nameAr = raw.nameAr,
                timeFormatted = formatArabicAmPm(timeFormatter.format(zoned)),
                epochMillis = raw.millis,
                isNext = (index == nextIdx) || (nextIdx == -1 && index == 0)
            )
        }

        return PrayerDashboardState(
            dayNameAr = dayName,
            hijriDateAr = hijriFormatted,
            gregorianDateAr = gregorianFormatted,
            nextPrayerName = nextPrayerName,
            nextPrayerTimeFormatted = nextTimeStr,
            remainingHoursMinutes = remainingHHMM,
            elapsedText = elapsedText,
            remainingText = remainingText,
            progress = progress,
            prayersList = singlePrayers
        )
    }

    private fun formatArabicAmPm(formatted: String): String {
        return formatted
            .replace("AM", "ص")
            .replace("PM", "م")
            .replace("ص", "ص")
            .replace("م", "م")
    }

    private fun convertToArabicDigits(input: String): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { ch ->
            if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
        }.joinToString("")
    }
}
