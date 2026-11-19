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

    val filteredSurahs = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.name.contains(searchQuery.trim()) ||
                it.id.toString() == searchQuery.trim()
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
                // Continue Reading Hero Card
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
                                .background(MindGoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bookmarkedSurah.id.toArabicDigits(),
                                color = MindBgDark,
                                fontFamily = CairoFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "ابحث باسم السورة أو رقمها...",
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = MindGoldAccent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MindGoldAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                            focusedTextColor = MindTextPrimary,
                            unfocusedTextColor = MindTextPrimary
                        )
                    )
                }

                // Surahs Index
                items(filteredSurahs, key = { it.id }) { surah ->
                    val typeAr = if (surah.type.lowercase(Locale.US) == "meccan") "مكية" else "مدنية"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MindInnerSurface)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
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

    val bgColor = if (isSepia) Color(0xFFF6EEDB) else MindBgDark
    val cardColor = if (isSepia) Color(0xFFEDE0C4) else MindInnerSurface
    val textColor = if (isSepia) Color(0xFF1F241D) else MindTextPrimary
    val accentColor = if (isSepia) Color(0xFF155D43) else MindGoldAccent

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
                // Font Size & Sepia Mode Controls
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
            // Ornamental Surah Header + Bismillah (except At-Tawbah #9)
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

            // Verses List with Ornamental End-of-Ayah Markers
            items(surah.verses, key = { it.id }) { verse ->
                val isBookmarked = (bookmark.surahId == surah.id && bookmark.verseId == verse.id)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isBookmarked) accentColor.copy(alpha = 0.14f)
                            else cardColor.copy(alpha = 0.65f)
                        )
                        .border(
                            width = if (isBookmarked) 1.dp else 0.dp,
                            color = if (isBookmarked) accentColor else Color.Transparent,
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

    // Ayah Action Dialog (Bookmark or Set as Home Screen Verse)
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
                            .background(Color.Black.copy(alpha = 0.22f))
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
                            .background(Color.Black.copy(alpha = 0.22f))
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
