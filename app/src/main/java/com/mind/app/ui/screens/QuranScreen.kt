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
import androidx.compose.material.icons.automirrored.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.FindInPage
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

private enum class QuranSearchScope {
    SURAH,
    WORD
}

private fun Int.toArabicDigits(): String {
    val arabicChars = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.toString().map { ch ->
        if (ch in '0'..'9') arabicChars[ch - '0'] else ch
    }.joinToString("")
}

private fun String.normalizeArabicForSearch(): String {
    val sb = StringBuilder(this.length)
    for (ch in this) {
        when (ch) {
            in '\u064B'..'\u065F', '\u0670', '\u0640', in '\u06D6'..'\u06ED' -> Unit
            'أ', 'إ', 'آ', 'ٱ' -> sb.append('ا')
            'ى', 'ئ' -> sb.append('ي')
            'ؤ' -> sb.append('و')
            else -> sb.append(ch)
        }
    }
    return sb.toString().replace(Regex("\\s+"), " ").trim()
}

private data class IndexedVerse(
    val surah: QuranSurah,
    val verse: QuranVerseItem,
    val normalizedText: String
)

private data class VerseSearchHit(
    val surah: QuranSurah,
    val verse: QuranVerseItem,
    val matchRank: Int
)

@Composable
fun QuranScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    DisposableEffect(Unit) {
        viewModel.setQuranTabActive(true)
        onDispose {
            viewModel.setQuranTabActive(false)
        }
    }

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
    var searchScope by remember { mutableStateOf(QuranSearchScope.SURAH) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(searchQuery) {
        delay(150L)
        debouncedQuery = searchQuery.normalizeArabicForSearch()
    }

    val flatSearchIndex by produceState<Pair<List<Pair<QuranSurah, String>>, List<IndexedVerse>>>(
        initialValue = Pair(emptyList(), emptyList()),
        key1 = surahs
    ) {
        value = withContext(Dispatchers.Default) {
            val surahNames = surahs.map { it to it.name.normalizeArabicForSearch() }
            val allVerses = ArrayList<IndexedVerse>(6236)
            for (s in surahs) {
                for (v in s.verses) {
                    allVerses.add(IndexedVerse(s, v, v.text.normalizeArabicForSearch()))
                }
            }
            Pair(surahNames, allVerses)
        }
    }

    val searchResults by produceState<Pair<List<QuranSurah>, List<VerseSearchHit>>>(
        initialValue = Pair(surahs, emptyList()),
        key1 = debouncedQuery,
        key2 = searchScope,
        key3 = flatSearchIndex
    ) {
        value = withContext(Dispatchers.Default) {
            val (surahNames, allVerses) = flatSearchIndex
            when (searchScope) {
                QuranSearchScope.SURAH -> {
                    val matchedSurahs = if (debouncedQuery.isBlank()) {
                        surahs
                    } else {
                        surahNames.filter { (s, normName) ->
                            normName.contains(debouncedQuery) || s.id.toString() == debouncedQuery
                        }.map { it.first }
                    }
                    Pair(matchedSurahs, emptyList())
                }

                QuranSearchScope.WORD -> {
                    if (debouncedQuery.isBlank()) {
                        Pair(emptyList(), emptyList())
                    } else {
                        val matchedVerses = allVerses.asSequence()
                            .filter { it.normalizedText.contains(debouncedQuery) }
                            .map { item ->
                                val rank = when {
                                    item.normalizedText.startsWith(debouncedQuery) -> 0
                                    item.normalizedText.endsWith(debouncedQuery) -> 1
                                    else -> 2
                                }
                                VerseSearchHit(item.surah, item.verse, rank)
                            }
                            .sortedWith(compareBy({ it.matchRank }, { it.surah.id }, { it.verse.id }))
                            .take(100)
                            .toList()
                        Pair(emptyList(), matchedVerses)
                    }
                }
            }
        }
    }

    val filteredSurahs = searchResults.first
    val verseHits = searchResults.second

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MindBgDark)
            ) {
                SubScreenTopBar(title = "المصحف الشريف", onBack = onBack)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Icon-only scope toggle: Surah vs Word
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MindInnerSurface)
                            .border(1.dp, MindGoldAccent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ScopeIconButton(
                            icon = Icons.AutoMirrored.Outlined.MenuBook,
                            selected = searchScope == QuranSearchScope.SURAH,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                searchScope = QuranSearchScope.SURAH
                            }
                        )
                        ScopeIconButton(
                            icon = Icons.Outlined.FindInPage,
                            selected = searchScope == QuranSearchScope.WORD,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                searchScope = QuranSearchScope.WORD
                            }
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (searchScope == QuranSearchScope.SURAH) "السورة" else "الكلمة",
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = MindGoldAccent)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Outlined.Close, contentDescription = null, tint = MindTextSecondary)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontFamily = CairoFontFamily,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            color = MindTextPrimary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MindGoldAccent,
                            unfocusedBorderColor = MindTextSecondary.copy(alpha = 0.25f),
                            focusedContainerColor = MindInnerSurface,
                            unfocusedContainerColor = MindInnerSurface,
                            focusedTextColor = MindTextPrimary,
                            unfocusedTextColor = MindTextPrimary
                        )
                    )
                }
            }
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
                contentPadding = PaddingValues(top = 6.dp, bottom = 32.dp)
            ) {
                if (searchScope == QuranSearchScope.SURAH && debouncedQuery.isBlank()) {
                    item(key = "continue_reading_card") {
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
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
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
                                        tint = MindGoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "متابعة القراءة",
                                        color = MindGoldAccent,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "سورة ${bookmarkedSurah.name}",
                                    color = MindTextPrimary,
                                    fontFamily = QuranFontFamily,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "الآية ${bookmark.verseId.toArabicDigits()}",
                                    color = MindTextSecondary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MindInnerSurface)
                                    .border(1.dp, MindGoldAccent.copy(alpha = 0.45f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bookmarkedSurah.id.toArabicDigits(),
                                    color = MindGoldAccent,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Word Search Results (ONLY when WORD icon is active)
                if (searchScope == QuranSearchScope.WORD) {
                    items(verseHits, key = { "v_${it.surah.id}_${it.verse.id}" }) { hit ->
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
                            Text(
                                text = "سورة ${hit.surah.name} • آية ${hit.verse.id.toArabicDigits()}",
                                color = MindGoldAccent,
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "﴿ ${hit.verse.text} ﴾",
                                color = MindTextPrimary,
                                fontFamily = QuranFontFamily,
                                fontSize = 21.sp,
                                lineHeight = 36.sp
                            )
                        }
                    }
                }

                // Surah Search Results (ONLY when SURAH icon is active)
                if (searchScope == QuranSearchScope.SURAH) {
                    items(filteredSurahs, key = { "s_${it.id}" }) { surah ->
                        val typeAr = if (surah.type.lowercase(Locale.US) == "meccan") "مكية" else "مدنية"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MindInnerSurface)
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
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
                                        .border(1.dp, MindGoldAccent.copy(alpha = 0.45f), CircleShape),
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
                                        fontSize = 22.sp
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
                                    contentDescription = null,
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
private fun ScopeIconButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MindGoldAccent else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MindInnerSurface else MindTextSecondary,
            modifier = Modifier.size(21.dp)
        )
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

    val bgColor = if (isSepia) Color(0xFFF6EEDB) else Color(0xFFE6F7F0)
    val cardColor = if (isSepia) Color(0xFFFAF6EE) else Color(0xFFF4FCF8)
    val textColor = if (isSepia) Color(0xFF133A2B) else Color(0xFF0E4230)
    val accentColor = if (isSepia) Color(0xFFA67714) else Color(0xFF127A56)

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
                        Icon(Icons.Outlined.Remove, contentDescription = null, tint = textColor)
                    }
                    IconButton(onClick = { onFontSizeChange(fontSize + 2f) }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = textColor)
                    }
                    IconButton(onClick = onToggleSepia) {
                        Icon(Icons.Outlined.Contrast, contentDescription = null, tint = accentColor)
                    }
                }

                Text(
                    text = "سورة ${surah.name}",
                    color = accentColor,
                    fontFamily = QuranFontFamily,
                    fontSize = 24.sp
                )

                IconButton(onClick = onBackToIndex) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
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
                        .border(1.5.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿ سُورَةُ ${surah.name} ﴾",
                        color = accentColor,
                        fontFamily = QuranFontFamily,
                        fontSize = 27.sp,
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
                            if (isBookmarked || isHighlighted) accentColor.copy(alpha = 0.14f)
                            else cardColor
                        )
                        .border(
                            width = if (isBookmarked || isHighlighted) 1.dp else 0.5.dp,
                            color = if (isBookmarked || isHighlighted) accentColor else accentColor.copy(alpha = 0.15f),
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
                        lineHeight = (fontSize * 1.8f).sp,
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
                                text = "علامة الوقف",
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
                            .background(MindBgDark.copy(alpha = 0.65f))
                            .clickable {
                                onSaveBookmark(verse.id)
                                selectedVerseForAction = null
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = MindGoldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("حفظ علامة الوقف", color = MindTextPrimary, fontFamily = CairoFontFamily)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MindBgDark.copy(alpha = 0.65f))
                            .clickable {
                                onSetHomeVerse(verse.text)
                                selectedVerseForAction = null
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = MindMintIcon)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("الواجهة الرئيسية", color = MindTextPrimary, fontFamily = CairoFontFamily)
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
