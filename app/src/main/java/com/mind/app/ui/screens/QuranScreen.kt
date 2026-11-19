package com.mind.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.app.domain.model.QuranBookmark
import com.mind.app.domain.model.QuranSurah
import com.mind.app.domain.model.QuranVerseItem
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindCardBottom
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary
import com.mind.app.ui.theme.QuranFontFamily
import java.util.Locale

private fun Int.toArabicDigits(): String {
    val arabicChars = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.toString().map { ch ->
        if (ch in '0'..'9') arabicChars[ch - '0'] else ch
    }.joinToString("")
}

/**
 * Normalizes Uthmani Arabic Quran text by stripping Tashkeel, Tatweel, and unifying Alef/Ya forms
 * so searching "يا ايها" matches "يَـٰٓأَيُّهَا" accurately.
 */
private fun String.normalizeArabicForSearch(): String {
    val sb = StringBuilder(this.length)
    for (ch in this) {
        when (ch) {
            in '\u064B'..'\u065F', '\u0670', '\u0640', in '\u06D6'..'\u06ED' -> Unit
            'أ', 'إ', 'آ', 'ٱ' -> sb.append('ا')
            'ى' -> sb.append('ي')
            'ؤ' -> sb.append('و')
            'ئ' -> sb.append('ي')
            else -> sb.append(ch)
        }
    }
    return sb.toString().replace(Regex("\\s+"), " ").trim()
}

private data class VerseSearchHit(
    val surah: QuranSurah,
    val verse: QuranVerseItem,
    val matchRank: Int // 0 = starts with query, 1 = ends with query, 2 = contains query
)

@Composable
fun QuranScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val surahs by viewModel.quranSurahs.collectAsState()
    val bookmark by viewModel.quranBookmark.collectAsState()
    val fontSize by viewModel.quranFontSize.collectAsState()
    val isSepia by viewModel.quranSepiaMode.collectAsState()

    var activeSurah by remember { mutableStateOf<QuranSurah?>(null) }
    var targetScrollVerse by remember { mutableStateOf<Int?>(null) }

    AnimatedContent(
        targetState = activeSurah,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "quran_navigation"
    ) { surah ->
        if (surah != null) {
            MushafSurahReaderScreen(
                surah = surah,
                initialVerseId = targetScrollVerse,
                bookmark = bookmark,
                fontSize = fontSize,
                isSepia = isSepia,
                onFontSizeChange = { viewModel.updateQuranFontSize(it) },
                onToggleSepia = { viewModel.toggleQuranSepiaMode(!isSepia) },
                onSaveBookmark = { verseId ->
                    viewModel.saveBookmark(surah.id, surah.name, verseId)
                },
                onSetHomeVerse = { verseText ->
                    viewModel.updateQuranVerse(verseText)
                },
                onBackToIndex = {
                    activeSurah = null
                    targetScrollVerse = null
                }
            )
        } else {
            MushafIndexScreen(
                surahs = surahs,
                bookmark = bookmark,
                onOpenSurah = { selectedSurah, verseId ->
                    targetScrollVerse = verseId
                    activeSurah = selectedSurah
                },
                onBack = onBack
            )
        }
    }
}

@Composable
private fun MushafIndexScreen(
    surahs: List<QuranSurah>,
    bookmark: QuranBookmark,
    onOpenSurah: (QuranSurah, Int?) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    // Pre-compute normalized verses once for instant smart search across all 6236 verses
    val normalizedIndex = remember(surahs) {
        surahs.map { surah ->
            val normSurahName = surah.name.normalizeArabicForSearch()
            val normVerses = surah.verses.map { v -> Triple(v, v.text.normalizeArabicForSearch(), surah) }
            Triple(surah, normSurahName, normVerses)
        }
    }

    val normalizedQuery = remember(searchQuery) { searchQuery.normalizeArabicForSearch() }

    val filteredSurahs = remember(normalizedIndex, normalizedQuery) {
        if (normalizedQuery.isBlank()) surahs
        else normalizedIndex.filter { (surah, normName, _) ->
            normName.contains(normalizedQuery) || surah.id.toString() == normalizedQuery
        }.map { it.first }
    }

    val verseHits = remember(normalizedIndex, normalizedQuery) {
        if (normalizedQuery.length < 2) emptyList()
        else {
            val results = mutableListOf<VerseSearchHit>()
            for ((surah, _, normVerses) in normalizedIndex) {
                for ((verse, normText, _) in normVerses) {
                    if (normText.contains(normalizedQuery)) {
                        val rank = when {
                            normText.startsWith(normalizedQuery) -> 0
                            normText.endsWith(normalizedQuery) -> 1
                            else -> 2
                        }
                        results.add(VerseSearchHit(surah, verse, rank))
                    }
                }
            }
            results.sortedWith(compareBy({ it.matchRank }, { it.surah.id }, { it.verse.id })).take(120)
        }
    }

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            SubScreenTopBar(title = "المصحف الشريف", onBack = onBack)
        }
    ) { padding ->
        if (surahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MindGoldAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Continue Reading Hero Card (when not searching)
                if (normalizedQuery.isBlank()) {
                    item {
                        val bookmarkedSurah = surahs.find { it.id == bookmark.surahId } ?: surahs.first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(MindCardTop, MindCardBottom)
                                    )
                                )
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onOpenSurah(bookmarkedSurah, bookmark.verseId)
                                }
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Bookmark,
                                        contentDescription = null,
                                        tint = Color(0xFFE5B94C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "متابعة القراءة",
                                        color = Color(0xFFE5B94C),
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "سورة ${bookmarkedSurah.name}",
                                    color = Color(0xFFF3F7F5),
                                    fontFamily = QuranFontFamily,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "الآية ${bookmark.verseId.toArabicDigits()}",
                                    color = Color(0xFFA7C4B9),
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5B94C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bookmarkedSurah.id.toArabicDigits(),
                                    color = Color(0xFF091C16),
                                    fontFamily = CairoFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Smart Search Bar (Surahs + Verses)
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "ابحث بسورة أو بآية (مثل: يا ايها...)",
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = MindGoldAccent)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = MindTextSecondary)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MindGoldAccent,
                            unfocusedBorderColor = MindTextSecondary.copy(alpha = 0.25f),
                            focusedTextColor = MindTextPrimary,
                            unfocusedTextColor = MindTextPrimary
                        )
                    )
                }

                // Matching Verses Section (when searching by ayah text)
                if (verseHits.isNotEmpty()) {
                    item {
                        Text(
                            text = "الآيات المطابقة (${verseHits.size.toArabicDigits()})",
                            color = MindGoldAccent,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(verseHits, key = { "${it.surah.id}_${it.verse.id}" }) { hit ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MindInnerSurface)
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onOpenSurah(hit.surah, hit.verse.id)
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سورة ${hit.surah.name} • آية ${hit.verse.id.toArabicDigits()}",
                                    color = MindGoldAccent,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val badge = when (hit.matchRank) {
                                    0 -> "بداية الآية"
                                    1 -> "نهاية الآية"
                                    else -> "في الآية"
                                }
                                Text(
                                    text = badge,
                                    color = MindTextSecondary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "﴿ ${hit.verse.text} ﴾",
                                color = MindTextPrimary,
                                fontFamily = QuranFontFamily,
                                fontSize = 20.sp,
                                lineHeight = 34.sp
                            )
                        }
                    }
                }

                // Surahs Index Section
                if (filteredSurahs.isNotEmpty()) {
                    if (verseHits.isNotEmpty()) {
                        item {
                            Text(
                                text = "السور المطابقة",
                                color = MindGoldAccent,
                                fontFamily = CairoFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            )
                        }
                    }

                    items(filteredSurahs, key = { it.id }) { surah ->
                        val typeAr = if (surah.type.lowercase(Locale.US) == "meccan") "مكية" else "مدنية"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MindInnerSurface)
                                .border(1.dp, MindTextSecondary.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onOpenSurah(surah, null)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MindGoldAccent.copy(alpha = 0.14f))
                                        .border(1.dp, MindGoldAccent.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = surah.id.toArabicDigits(),
                                        color = MindGoldAccent,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "سورة ${surah.name}",
                                        color = MindTextPrimary,
                                        fontFamily = QuranFontFamily,
                                        fontSize = 21.sp
                                    )
                                    Text(
                                        text = "$typeAr • ${surah.total_verses.toArabicDigits()} آية",
                                        color = MindTextSecondary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (bookmark.surahId == surah.id) {
                                Icon(
                                    imageVector = Icons.Outlined.Bookmark,
                                    contentDescription = "Bookmarked",
                                    tint = MindGoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MushafSurahReaderScreen(
    surah: QuranSurah,
    initialVerseId: Int?,
    bookmark: QuranBookmark,
    fontSize: Float,
    isSepia: Boolean,
    onFontSizeChange: (Float) -> Unit,
    onToggleSepia: () -> Unit,
    onSaveBookmark: (Int) -> Unit,
    onSetHomeVerse: (String) -> Unit,
    onBackToIndex: () -> Unit
) {
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    var selectedVerseForAction by remember { mutableStateOf<QuranVerseItem?>(null) }

    val bgColor = if (isSepia) Color(0xFFF6EEDB) else Color(0xFF091C16)
    val cardColor = if (isSepia) Color(0xFFEDE0C4) else Color(0xFF0E3F2E)
    val textColor = if (isSepia) Color(0xFF1F241D) else Color(0xFFF3F7F5)
    val accentColor = if (isSepia) Color(0xFF155D43) else Color(0xFFE5B94C)

    LaunchedEffect(surah.id, initialVerseId) {
        if (initialVerseId != null && initialVerseId > 1) {
            val idx = surah.verses.indexOfFirst { it.id == initialVerseId }
            if (idx >= 0) {
                listState.scrollToItem(idx + 1)
            }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onFontSizeChange(fontSize - 2f) }) {
                        Icon(Icons.Outlined.Remove, contentDescription = "Smaller Font", tint = textColor)
                    }
                    IconButton(onClick = { onFontSizeChange(fontSize + 2f) }) {
                        Icon(Icons.Outlined.Add, contentDescription = "Larger Font", tint = textColor)
                    }
                    IconButton(onClick = onToggleSepia) {
                        Icon(Icons.Outlined.Contrast, contentDescription = "Reading Theme", tint = accentColor)
                    }
                }

                Text(
                    text = "سورة ${surah.name}",
                    color = accentColor,
                    fontFamily = QuranFontFamily,
                    fontSize = 23.sp
                )

                IconButton(onClick = onBackToIndex) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(cardColor)
                        .border(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿ سُورَةُ ${surah.name} ﴾",
                        color = accentColor,
                        fontFamily = QuranFontFamily,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                    if (surah.id != 1 && surah.id != 9) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
                            color = textColor,
                            fontFamily = QuranFontFamily,
                            fontSize = (fontSize + 1f).sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            items(surah.verses, key = { it.id }) { verse ->
                val isBookmarked = (bookmark.surahId == surah.id && bookmark.verseId == verse.id)
                val isHighlighted = (initialVerseId == verse.id)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isBookmarked || isHighlighted) accentColor.copy(alpha = 0.16f)
                            else cardColor.copy(alpha = 0.65f)
                        )
                        .border(
                            width = if (isBookmarked || isHighlighted) 1.dp else 0.dp,
                            color = if (isBookmarked || isHighlighted) accentColor else Color.Transparent,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedVerseForAction = verse
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    val annotatedVerse = buildAnnotatedString {
                        append(verse.text)
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("﴿${verse.id.toArabicDigits()}﴾")
                        }
                    }

                    Text(
                        text = annotatedVerse,
                        color = textColor,
                        fontFamily = QuranFontFamily,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.75f).sp,
                        textAlign = TextAlign.Justify
                    )

                    if (isBookmarked) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Bookmark,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "علامة الوقف المحفوظة",
                                color = accentColor,
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    selectedVerseForAction?.let { verse ->
        AlertDialog(
            onDismissRequest = { selectedVerseForAction = null },
            containerColor = MindInnerSurface,
            title = {
                Text(
                    text = "سورة ${surah.name} • آية ${verse.id.toArabicDigits()}",
                    color = MindGoldAccent,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MindBgDark.copy(alpha = 0.45f))
                            .clickable {
                                onSaveBookmark(verse.id)
                                selectedVerseForAction = null
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = MindGoldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("حفظ علامة الوقف هنا", color = MindTextPrimary, fontFamily = CairoFontFamily)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MindBgDark.copy(alpha = 0.45f))
                            .clickable {
                                onSetHomeVerse(verse.text)
                                selectedVerseForAction = null
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = MindMintIcon)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("عرض هذه الآية في الواجهة الرئيسية", color = MindTextPrimary, fontFamily = CairoFontFamily)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedVerseForAction = null }) {
                    Text("إغلاق", color = MindTextSecondary, fontFamily = CairoFontFamily)
                }
            }
        )
    }
}
