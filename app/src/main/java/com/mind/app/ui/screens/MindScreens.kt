package com.mind.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.components.DynamicQuoteSpace
import com.mind.app.ui.components.MinimalBottomActionBar
import com.mind.app.ui.components.MindTopBar
import com.mind.app.ui.components.PrayerDashboardCard
import com.mind.app.ui.components.QuranVerseHeader
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary

@Composable
fun HomeScreen(
    viewModel: MindViewModel,
    onNavigateToNotes: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val prayerState by viewModel.prayerState.collectAsState()
    val quranVerse by viewModel.quranVerse.collectAsState()
    val activeQuote by viewModel.activeHomeQuote.collectAsState()

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                MindTopBar(onSettingsClick = onNavigateToSettings)
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                MinimalBottomActionBar(
                    onNotesClick = onNavigateToNotes,
                    onTasksClick = onNavigateToTasks,
                    onQuotesEditorClick = onNavigateToQuotes
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Quran Verse
            QuranVerseHeader(verse = quranVerse)

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Prayer & Date Card
            PrayerDashboardCard(state = prayerState)

            // 3. Invisible-until-active Quote Space
            DynamicQuoteSpace(
                quote = activeQuote,
                onQuoteTap = { viewModel.shuffleRandomQuote() }
            )
        }
    }
}

@Composable
fun SubScreenTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        Text(
            text = title,
            color = MindTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Back",
                tint = MindTextPrimary
            )
        }
    }
}

@Composable
fun QuotesEditorScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val quotes by viewModel.quotes.collectAsState()
    val mode by viewModel.quoteMode.collectAsState()
    val pinnedId by viewModel.pinnedQuoteId.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "مساحة العقل والاقتباسات", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Editor Input Box
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "اكتب اقتباساً أو فكرة تهمك لتظهر في عقلك...",
                        color = MindTextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindGoldAccent,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = MindTextPrimary,
                    unfocusedTextColor = MindTextPrimary,
                    cursorColor = MindGoldAccent
                ),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Switcher: Pinned vs Random
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == QuoteDisplayMode.PINNED,
                        onClick = { viewModel.setQuoteDisplayMode(QuoteDisplayMode.PINNED) },
                        label = { Text("مثبت") },
                        leadingIcon = {
                            Icon(Icons.Outlined.PushPin, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MindGoldAccent,
                            selectedLabelColor = MindBgDark,
                            selectedLeadingIconColor = MindBgDark,
                            labelColor = MindTextSecondary
                        )
                    )
                    FilterChip(
                        selected = mode == QuoteDisplayMode.RANDOM,
                        onClick = { viewModel.setQuoteDisplayMode(QuoteDisplayMode.RANDOM) },
                        label = { Text("عشوائي") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MindMintIcon,
                            selectedLabelColor = MindBgDark,
                            selectedLeadingIconColor = MindBgDark,
                            labelColor = MindTextSecondary
                        )
                    )
                }

                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addQuote(inputText, pinImmediately = (mode == QuoteDisplayMode.PINNED))
                            inputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MindGoldAccent,
                        contentColor = MindBgDark
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حفظ", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(quotes, key = { it.id }) { item ->
                    val isPinned = (mode == QuoteDisplayMode.PINNED && pinnedId == item.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MindInnerSurface)
                            .border(
                                width = if (isPinned) 1.5.dp else 0.dp,
                                color = if (isPinned) MindGoldAccent else Color.Transparent,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Pin on Home button with Quote Icon
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.pinQuote(item.id)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FormatQuote,
                                contentDescription = "Pin on Home",
                                tint = if (isPinned) MindGoldAccent else MindTextSecondary
                            )
                        }

                        Text(
                            text = item.text,
                            color = MindTextPrimary,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            lineHeight = 24.sp
                        )

                        IconButton(onClick = { viewModel.deleteQuote(item.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MindTextSecondary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val notes by viewModel.notes.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "التدوينات", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("عنوان التدوينة...", color = MindTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindMintIcon,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = MindTextPrimary,
                    unfocusedTextColor = MindTextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("فرّغ أفكارك هنا...", color = MindTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindMintIcon,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = MindTextPrimary,
                    unfocusedTextColor = MindTextPrimary
                ),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        viewModel.addNote(title, content)
                        title = ""
                        content = ""
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = MindMintIcon, contentColor = MindBgDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("حفظ التدوينة", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MindInnerSurface)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = note.title,
                                color = MindGoldAccent,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                                Icon(
                                    Icons.Outlined.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MindTextSecondary
                                )
                            }
                        }
                        if (note.content.isNotBlank()) {
                            Text(
                                text = note.content,
                                color = MindTextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    var taskText by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "المهام اليومية", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskText,
                    onValueChange = { taskText = it },
                    placeholder = { Text("أضف مهمة جديدة...", color = MindTextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MindGoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = MindTextPrimary,
                        unfocusedTextColor = MindTextPrimary
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (taskText.isNotBlank()) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addTask(taskText)
                            taskText = ""
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MindGoldAccent)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add Task", tint = MindBgDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MindInnerSurface)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.toggleTask(task.id)
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (task.isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (task.isDone) MindMintIcon else MindTextSecondary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = task.title,
                                color = if (task.isDone) MindTextSecondary else MindTextPrimary,
                                fontSize = 16.sp,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                        IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MindTextSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val currentVerse by viewModel.quranVerse.collectAsState()
    var verseInput by remember(currentVerse) { mutableStateOf(currentVerse) }

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "الإعدادات", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "الآية القرآنية في الواجهة الرئيسية",
                color = MindGoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = verseInput,
                onValueChange = { verseInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindGoldAccent,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = MindTextPrimary,
                    unfocusedTextColor = MindTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    viewModel.updateQuranVerse(verseInput)
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MindMintIcon, contentColor = MindBgDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("حفظ التعديل", fontWeight = FontWeight.Bold)
            }
        }
    }
}
