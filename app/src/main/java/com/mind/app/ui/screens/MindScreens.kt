package com.mind.app.ui.screens

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mind.app.domain.model.QuoteDisplayMode
import com.mind.app.domain.model.TaskItem
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.components.DynamicQuoteSpace
import com.mind.app.ui.components.MinimalBottomActionBar
import com.mind.app.ui.components.MindTopBar
import com.mind.app.ui.components.PrayerDashboardCard
import com.mind.app.ui.components.QuranVerseHeader
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToQuran: () -> Unit,
    viewModel: MindViewModel,
    onNavigateToNotes: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToNutrition: () -> Unit = {},
    onNavigateToChannels: () -> Unit = {},
    onNavigateToQuotes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val isMusicPlaying by viewModel.isUserMusicToggleOn.collectAsState()
    val prayerState by viewModel.prayerState.collectAsState()
    val quranVerse by viewModel.quranVerse.collectAsState()
    val activeQuote by viewModel.activeHomeQuote.collectAsState()

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                MindTopBar(isMusicPlaying = isMusicPlaying, onToggleMusic = { viewModel.toggleAmbientMusic() }, onSettingsClick = onNavigateToSettings)
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                MinimalBottomActionBar(
                    onQuranClick = onNavigateToQuran,
                    onNotesClick = onNavigateToNotes,
                    onTasksClick = onNavigateToTasks,
                onNutritionClick = onNavigateToNutrition,
                onChannelsClick = onNavigateToChannels,
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
            QuranVerseHeader(verse = quranVerse)
            Spacer(modifier = Modifier.height(6.dp))
            PrayerDashboardCard(state = prayerState)
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
            fontFamily = CairoFontFamily,
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
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "اكتب اقتباساً أو فكرة تهمك لتظهر في عقلك...",
                        color = MindTextSecondary,
                        fontFamily = CairoFontFamily
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == QuoteDisplayMode.PINNED,
                        onClick = { viewModel.setQuoteDisplayMode(QuoteDisplayMode.PINNED) },
                        label = { Text("مثبت", fontFamily = CairoFontFamily) },
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
                        label = { Text("عشوائي", fontFamily = CairoFontFamily) },
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
                    Text("حفظ", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
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
                            fontFamily = CairoFontFamily,
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

private data class DraftTask(
    val title: String = "",
    val imagePath: String? = null,
    val dueAtMillis: Long? = null
)

/**
 * Multi-Day Tasks Screen ("المهام"):
 * - Supports adding multiple tasks at once (+ adds another draft row, ✓ saves all)
 * - Supports attaching a single image per task
 * - Supports scheduling exact date & time alarm (shows full-screen overlay over other apps)
 */
@Composable
fun TasksScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    val drafts = remember { mutableStateListOf(DraftTask()) }
    var pickingImageForIndex by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val singleImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && pickingImageForIndex in drafts.indices) {
            val targetIdx = pickingImageForIndex
            scope.launch {
                val path = viewModel.importImage(uri)
                if (path != null && targetIdx in drafts.indices) {
                    drafts[targetIdx] = drafts[targetIdx].copy(imagePath = path)
                }
            }
        }
    }

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "المهام", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Batch Multi-Task Composer Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(MindInnerSurface)
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    drafts.forEachIndexed { idx, draft ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.22f))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = draft.title,
                                    onValueChange = { drafts[idx] = draft.copy(title = it) },
                                    placeholder = {
                                        Text(
                                            text = if (drafts.size > 1) "مهمة ${idx + 1}..." else "اكتب المهمة هنا...",
                                            color = MindTextSecondary,
                                            fontFamily = CairoFontFamily
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MindGoldAccent,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                        focusedTextColor = MindTextPrimary,
                                        unfocusedTextColor = MindTextPrimary
                                    ),
                                    singleLine = true
                                )

                                if (drafts.size > 1) {
                                    IconButton(onClick = { drafts.removeAt(idx) }) {
                                        Icon(Icons.Outlined.Close, contentDescription = "Remove Row", tint = MindTextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Single Image + Date/Time Alarm Picker Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Single Image Only Button
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (draft.imagePath != null) MindMintIcon.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                                            .clickable {
                                                pickingImageForIndex = idx
                                                singleImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.Image, contentDescription = "Add Photo", tint = MindMintIcon, modifier = Modifier.size(18.dp))
                                            if (draft.imagePath != null) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("صورة مرفقة", color = MindMintIcon, fontFamily = CairoFontFamily, fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    // Multi-Day Date & Time Alarm Picker
                                    val alarmLabel = remember(draft.dueAtMillis) {
                                        draft.dueAtMillis?.let {
                                            SimpleDateFormat("d MMM • h:mm a", Locale("ar", "EG")).format(Date(it))
                                        } ?: "تحديد وقت التنبيه"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (draft.dueAtMillis != null) MindGoldAccent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                                            .clickable {
                                                val cal = Calendar.getInstance()
                                                DatePickerDialog(
                                                    context,
                                                    { _, year, month, day ->
                                                        TimePickerDialog(
                                                            context,
                                                            { _, hour, minute ->
                                                                cal.set(year, month, day, hour, minute, 0)
                                                                drafts[idx] = draft.copy(dueAtMillis = cal.timeInMillis)
                                                            },
                                                            cal.get(Calendar.HOUR_OF_DAY),
                                                            cal.get(Calendar.MINUTE),
                                                            false
                                                        ).show()
                                                    },
                                                    cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DAY_OF_MONTH)
                                                ).show()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.Alarm, contentDescription = "Alarm Time", tint = MindGoldAccent, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(alarmLabel, color = MindGoldAccent, fontFamily = CairoFontFamily, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Composer Actions: (+) Add Another Task Row | (✓) Save All Tasks
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                drafts.add(DraftTask())
                            },
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MindGoldAccent.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = "Add More", tint = MindGoldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة مهمة أخرى", color = MindGoldAccent, fontFamily = CairoFontFamily, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val toSave = drafts.filter { it.title.isNotBlank() }.map {
                                    TaskItem(
                                        title = it.title.trim(),
                                        imagePath = it.imagePath,
                                        dueAtMillis = it.dueAtMillis
                                    )
                                }
                                if (toSave.isNotEmpty()) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.addBatchTasks(toSave)
                                    drafts.clear()
                                    drafts.add(DraftTask())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MindGoldAccent, contentColor = MindBgDark),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = "Save Tasks", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حفظ", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Saved Tasks List
            items(tasks, key = { it.id }) { task ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MindInnerSurface)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleTask(task.id)
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            Column {
                                Text(
                                    text = task.title,
                                    color = if (task.isDone) MindTextSecondary else MindTextPrimary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                                )
                                if (task.dueAtMillis != null) {
                                    val timeStr = remember(task.dueAtMillis) {
                                        SimpleDateFormat("EEEE d MMM • h:mm a", Locale("ar", "EG")).format(Date(task.dueAtMillis))
                                    }
                                    Text(
                                        text = "⏰ $timeStr",
                                        color = MindGoldAccent,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MindTextSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (task.imagePath != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AsyncImage(
                            model = File(task.imagePath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val isMintTheme by viewModel.mintThemeEnabled.collectAsState()
    val currentVerse by viewModel.quranVerse.collectAsState()
    val adhanEnabled by viewModel.adhanEnabled.collectAsState()
    val adhanToneUri by viewModel.adhanToneUri.collectAsState()
    val adhanToneName by viewModel.adhanToneName.collectAsState()
    val taskToneUri by viewModel.taskToneUri.collectAsState()
    val taskToneName by viewModel.taskToneName.collectAsState()

    var verseInput by remember(currentVerse) { mutableStateOf(currentVerse) }
    val context = LocalContext.current

    // Phone Ringtone Picker for Adhan
    val adhanRingtonePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            val title = uri?.let { RingtoneManager.getRingtone(context, it)?.getTitle(context) } ?: "صوت افتراضي"
            viewModel.updateAdhanTone(uri?.toString(), title)
        }
    }

    // Phone Ringtone Picker for Tasks
    val taskRingtonePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            val title = uri?.let { RingtoneManager.getRingtone(context, it)?.getTitle(context) } ?: "نغمة افتراضية"
            viewModel.updateTaskTone(uri?.toString(), title)
        }
    }

    Scaffold(
        containerColor = MindBgDark,
        topBar = { SubScreenTopBar(title = "الإعدادات", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 0. Warm Sepia Light Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(MindInnerSurface).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الثيم النعناعي المبهج (Fresh Mint)", color = MindTextPrimary, fontFamily = CairoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Switch(checked = isMintTheme, onCheckedChange = { viewModel.toggleMintTheme(it) }, colors = SwitchDefaults.colors(checkedThumbColor = MindBgDark, checkedTrackColor = MindGoldAccent))
            }

            // 1. Adhan Audio & Auto-Alarm Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MindInnerSurface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Mosque, contentDescription = null, tint = MindGoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الأذان التلقائي للصلوات",
                            color = MindTextPrimary,
                            fontFamily = CairoFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = adhanEnabled,
                        onCheckedChange = { viewModel.toggleAdhanEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = MindBgDark, checkedTrackColor = MindGoldAccent)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "صوت الأذان المختار: $adhanToneName",
                    color = MindTextSecondary,
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "اختر صوت الأذان من الهاتف")
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, adhanToneUri?.let { Uri.parse(it) })
                        }
                        adhanRingtonePicker.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MindGoldAccent, contentColor = MindBgDark),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("اختيار الأذان من الهاتف", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            }

            // 2. Task Alarm Ringtone Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MindInnerSurface)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = MindMintIcon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نغمة تنبيه المهام",
                        color = MindTextPrimary,
                        fontFamily = CairoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "النغمة المختارة: $taskToneName",
                    color = MindTextSecondary,
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "اختر نغمة تنبيه المهام")
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, taskToneUri?.let { Uri.parse(it) })
                        }
                        taskRingtonePicker.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MindMintIcon, contentColor = MindBgDark),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("اختيار نغمة المهام", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            }

            // 3. System Overlay Permission (Display over other apps)
            if (!Settings.canDrawOverlays(context)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MindInnerSurface)
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Layers, contentDescription = null, tint = MindGoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إذن الظهور فوق التطبيقات",
                            color = MindTextPrimary,
                            fontFamily = CairoFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مطلوب لعرض شاشة الأذان وتنبيهات المهام فوق الشاشة والتطبيقات الأخرى.",
                        color = MindTextSecondary,
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("تفعيل الإذن الآن", color = MindGoldAccent, fontFamily = CairoFontFamily)
                    }
                }
            }

            // 4. Quran Verse Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MindInnerSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "الآية القرآنية في الواجهة الرئيسية",
                    color = MindGoldAccent,
                    fontFamily = CairoFontFamily,
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
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.updateQuranVerse(verseInput)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MindMintIcon, contentColor = MindBgDark),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("حفظ الآية", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
