package com.mind.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mind.app.domain.model.NoteFolder
import com.mind.app.domain.model.NoteItem
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.components.EmbeddedAudioPlaylistPlayer
import com.mind.app.ui.components.EmbeddedYouTubeVideoPlayer
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
import java.util.Date
import java.util.Locale

/**
 * Classic Twitter Bird Vector Icon in MindGoldAccent (replaces default "تدوينة" title)
 */
@Composable
fun ClassicTwitterBirdIcon(
    tint: Color = MindGoldAccent,
    modifier: Modifier = Modifier.size(22.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.98f, h * 0.19f)
            cubicTo(w * 0.94f, h * 0.21f, w * 0.90f, h * 0.22f, w * 0.86f, h * 0.23f)
            cubicTo(w * 0.90f, h * 0.20f, w * 0.94f, h * 0.16f, w * 0.95f, h * 0.11f)
            cubicTo(w * 0.91f, h * 0.14f, w * 0.86f, h * 0.15f, w * 0.82f, h * 0.16f)
            cubicTo(w * 0.78f, h * 0.12f, w * 0.72f, h * 0.09f, w * 0.66f, h * 0.09f)
            cubicTo(w * 0.54f, h * 0.09f, w * 0.45f, h * 0.19f, w * 0.45f, h * 0.30f)
            cubicTo(w * 0.45f, h * 0.32f, w * 0.45f, h * 0.34f, w * 0.46f, h * 0.35f)
            cubicTo(w * 0.28f, h * 0.34f, w * 0.13f, h * 0.26f, w * 0.03f, h * 0.13f)
            cubicTo(w * 0.01f, h * 0.16f, w * 0.00f, h * 0.20f, w * 0.00f, h * 0.24f)
            cubicTo(w * 0.00f, h * 0.31f, w * 0.04f, h * 0.38f, w * 0.09f, h * 0.41f)
            cubicTo(w * 0.06f, h * 0.41f, w * 0.02f, h * 0.40f, w * 0.00f, h * 0.39f)
            lineTo(w * 0.00f, h * 0.39f)
            cubicTo(w * 0.00f, h * 0.49f, w * 0.07f, h * 0.58f, w * 0.17f, h * 0.60f)
            cubicTo(w * 0.15f, h * 0.60f, w * 0.13f, h * 0.60f, w * 0.11f, h * 0.60f)
            cubicTo(w * 0.10f, h * 0.60f, w * 0.08f, h * 0.60f, w * 0.07f, h * 0.60f)
            cubicTo(w * 0.10f, h * 0.68f, w * 0.18f, h * 0.74f, w * 0.27f, h * 0.74f)
            cubicTo(w * 0.20f, h * 0.80f, w * 0.11f, h * 0.83f, w * 0.01f, h * 0.83f)
            cubicTo(w * 0.00f, h * 0.83f, w * 0.00f, h * 0.83f, w * 0.00f, h * 0.83f)
            cubicTo(w * 0.09f, h * 0.89f, w * 0.20f, h * 0.93f, w * 0.32f, h * 0.93f)
            cubicTo(w * 0.71f, h * 0.93f, w * 0.92f, h * 0.60f, w * 0.92f, h * 0.32f)
            cubicTo(w * 0.92f, h * 0.31f, w * 0.92f, h * 0.30f, w * 0.92f, h * 0.29f)
            cubicTo(w * 0.96f, h * 0.26f, w * 0.98f, h * 0.23f, w * 0.98f, h * 0.19f)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(viewModel: MindViewModel, onBack: () -> Unit) {
    val folders by viewModel.folders.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedFolderId by remember { mutableStateOf<String?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderBeingManaged by remember { mutableStateOf<NoteFolder?>(null) }
    var noteBeingMoved by remember { mutableStateOf<NoteItem?>(null) }
    var editingNote by remember { mutableStateOf<NoteItem?>(null) }

    AnimatedContent(
        targetState = editingNote,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "notes_editor_switch"
    ) { activeNote ->
        if (activeNote != null) {
            RichNoteEditorScreen(
                initialNote = activeNote,
                folders = folders,
                viewModel = viewModel,
                onClose = { savedNote ->
                    if (savedNote != null) {
                        viewModel.upsertNote(savedNote)
                    }
                    editingNote = null
                }
            )
        } else {
            val filteredNotes = remember(notes, selectedFolderId) {
                if (selectedFolderId == null) notes
                else notes.filter { it.folderId == selectedFolderId }
            }

            Scaffold(
                containerColor = MindBgDark,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showCreateFolderDialog = true
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MindInnerSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CreateNewFolder,
                                contentDescription = "Create Folder",
                                tint = MindGoldAccent
                            )
                        }

                        Text(
                            text = folders.find { it.id == selectedFolderId }?.name ?: "التدوينات",
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
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            editingNote = NoteItem(folderId = selectedFolderId)
                        },
                        containerColor = MindGoldAccent,
                        contentColor = MindBgDark,
                        shape = CircleShape,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "New Note", modifier = Modifier.size(28.dp))
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    AnimatedVisibility(
                        visible = folders.isNotEmpty(),
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                val isAllSelected = selectedFolderId == null
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isAllSelected) MindMintIcon else MindInnerSurface)
                                        .clickable { selectedFolderId = null }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "الكل",
                                        color = if (isAllSelected) MindBgDark else MindTextPrimary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            items(folders, key = { it.id }) { folder ->
                                val isSelected = selectedFolderId == folder.id
                                Row(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) MindGoldAccent else MindInnerSurface)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) MindGoldAccent else Color.White.copy(alpha = 0.08f),
                                            shape = CircleShape
                                        )
                                        .combinedClickable(
                                            onClick = {
                                                selectedFolderId = if (isSelected) null else folder.id
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                folderBeingManaged = folder
                                            }
                                        )
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = null,
                                        tint = if (isSelected) MindBgDark else MindGoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = folder.name,
                                        color = if (isSelected) MindBgDark else MindTextPrimary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    if (filteredNotes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "مساحتك هادئة وجاهزة لأفكارك...\nاضغط على (+) لبدء تدوينة جديدة",
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 15.sp,
                                lineHeight = 26.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredNotes, key = { it.id }) { note ->
                                val folderName = folders.find { it.id == note.folderId }?.name
                                RichNoteCard(
                                    note = note,
                                    folderName = folderName,
                                    hasFolders = folders.isNotEmpty(),
                                    onTapToEdit = { editingNote = note },
                                    onMoveRequest = { noteBeingMoved = note },
                                    onMoveAudioUp = { idx ->
                                        val list = note.audioPlaylist.toMutableList()
                                        if (idx > 0) {
                                            val item = list.removeAt(idx)
                                            list.add(idx - 1, item)
                                            viewModel.upsertNote(note.copy(audioPlaylist = list))
                                        }
                                    },
                                    onMoveAudioDown = { idx ->
                                        val list = note.audioPlaylist.toMutableList()
                                        if (idx < list.lastIndex) {
                                            val item = list.removeAt(idx)
                                            list.add(idx + 1, item)
                                            viewModel.upsertNote(note.copy(audioPlaylist = list))
                                        }
                                    },
                                    onDelete = { viewModel.deleteNote(note.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateFolderDialog) {
        FolderNameDialog(
            title = "مجلد جديد",
            initialName = "",
            confirmLabel = "إنشاء",
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { name ->
                viewModel.createFolder(name)
                showCreateFolderDialog = false
            }
        )
    }

    folderBeingManaged?.let { folder ->
        FolderManageDialog(
            folder = folder,
            onDismiss = { folderBeingManaged = null },
            onRename = { newName ->
                viewModel.renameFolder(folder.id, newName)
                folderBeingManaged = null
            },
            onDelete = {
                if (selectedFolderId == folder.id) selectedFolderId = null
                viewModel.deleteFolder(folder.id)
                folderBeingManaged = null
            }
        )
    }

    // One-tap Move Note to Folder Dialog
    noteBeingMoved?.let { note ->
        MoveNoteFolderDialog(
            folders = folders,
            currentFolderId = note.folderId,
            onDismiss = { noteBeingMoved = null },
            onSelectFolder = { targetId ->
                viewModel.moveNoteToFolder(note.id, targetId)
                noteBeingMoved = null
            }
        )
    }
}

@Composable
private fun RichNoteCard(
    note: NoteItem,
    folderName: String?,
    hasFolders: Boolean,
    onTapToEdit: () -> Unit,
    onMoveRequest: () -> Unit,
    onMoveAudioUp: (Int) -> Unit,
    onMoveAudioDown: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(note.updatedAt) {
        SimpleDateFormat("d MMM • h:mm a", Locale("ar", "EG")).format(Date(note.updatedAt))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MindInnerSurface)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTapToEdit() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (note.title.isBlank()) {
                        // Classic Twitter Bird Icon in Gold instead of "تدوينة"
                        ClassicTwitterBirdIcon(
                            tint = MindGoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = note.title,
                            color = MindGoldAccent,
                            fontFamily = CairoFontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (folderName != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = folderName,
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasFolders) {
                        IconButton(onClick = onMoveRequest, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.DriveFileMove,
                                contentDescription = "Move to Folder",
                                tint = MindGoldAccent.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MindTextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.content,
                    color = MindTextPrimary,
                    fontFamily = CairoFontFamily,
                    fontSize = 15.sp,
                    lineHeight = 25.sp
                )
            }
        }

        if (note.imagePaths.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(note.imagePaths) { path ->
                    AsyncImage(
                        model = File(path),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 180.dp, height = 130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onTapToEdit() }
                    )
                }
            }
        }

        if (note.videoPath != null) {
            Spacer(modifier = Modifier.height(12.dp))
            EmbeddedYouTubeVideoPlayer(videoPath = note.videoPath)
        }

        if (note.audioPlaylist.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            EmbeddedAudioPlaylistPlayer(
                playlist = note.audioPlaylist,
                isEditMode = false,
                onMoveTrackUp = onMoveAudioUp,
                onMoveTrackDown = onMoveAudioDown
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = dateStr,
            color = MindTextSecondary.copy(alpha = 0.65f),
            fontFamily = CairoFontFamily,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun RichNoteEditorScreen(
    initialNote: NoteItem,
    folders: List<NoteFolder>,
    viewModel: MindViewModel,
    onClose: (NoteItem?) -> Unit
) {
    var title by remember { mutableStateOf(initialNote.title) }
    var content by remember { mutableStateOf(initialNote.content) }
    var folderId by remember { mutableStateOf(initialNote.folderId) }
    var imagePaths by remember { mutableStateOf(initialNote.imagePaths) }
    var videoPath by remember { mutableStateOf(initialNote.videoPath) }
    var audioPlaylist by remember { mutableStateOf(initialNote.audioPlaylist) }
    var showFolderPicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val imported = uris.mapNotNull { viewModel.importImage(it) }
                imagePaths = imagePaths + imported
            }
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                viewModel.importVideo(uri)?.let { videoPath = it }
            }
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val tracks = uris.mapNotNull { viewModel.importAudio(it) }
                audioPlaylist = audioPlaylist + tracks
            }
        }
    }

    val hasAnyContent = title.isNotBlank() || content.isNotBlank() ||
        imagePaths.isNotEmpty() || videoPath != null || audioPlaylist.isNotEmpty()

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (hasAnyContent) {
                                onClose(
                                    initialNote.copy(
                                        folderId = folderId,
                                        title = title.trim(),
                                        content = content.trim(),
                                        imagePaths = imagePaths,
                                        videoPath = videoPath,
                                        audioPlaylist = audioPlaylist
                                    )
                                )
                            } else {
                                onClose(null)
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MindGoldAccent)
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = "Save", tint = MindBgDark)
                    }

                    if (folders.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showFolderPicker = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MindInnerSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FolderOpen,
                                contentDescription = "Select Folder",
                                tint = MindGoldAccent
                            )
                        }
                    }
                }

                val selectedFolderName = folders.find { it.id == folderId }?.name
                Text(
                    text = selectedFolderName ?: "مساحة الكتابة",
                    color = MindTextSecondary,
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp
                )

                IconButton(onClick = { onClose(null) }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cancel", tint = MindTextPrimary)
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 42.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MindInnerSurface)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Outlined.Image, contentDescription = "Add Image", tint = MindMintIcon, modifier = Modifier.size(26.dp))
                    }

                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                    }) {
                        Icon(Icons.Outlined.Videocam, contentDescription = "Add Video", tint = MindGoldAccent, modifier = Modifier.size(26.dp))
                    }

                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        audioPicker.launch("audio/*")
                    }) {
                        Icon(Icons.Outlined.AudioFile, contentDescription = "Add Audio", tint = MindTextPrimary, modifier = Modifier.size(26.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    color = MindGoldAccent,
                    fontFamily = CairoFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(MindGoldAccent),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "عنوان الفكرة (اختياري)...",
                            color = MindTextSecondary.copy(alpha = 0.45f),
                            fontFamily = CairoFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    color = MindTextPrimary,
                    fontFamily = CairoFontFamily,
                    fontSize = 17.sp,
                    lineHeight = 29.sp
                ),
                cursorBrush = SolidColor(MindMintIcon),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "اكتب بحرية هنا... أضف صوراً أو فيديو أو قائمة صوتيات من الأيقونات بالأسفل",
                            color = MindTextSecondary.copy(alpha = 0.45f),
                            fontFamily = CairoFontFamily,
                            fontSize = 16.sp,
                            lineHeight = 28.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (imagePaths.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(imagePaths) { path ->
                        Box {
                            AsyncImage(
                                model = File(path),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            IconButton(
                                onClick = { imagePaths = imagePaths.filterNot { it == path } },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            if (videoPath != null) {
                Spacer(modifier = Modifier.height(16.dp))
                EmbeddedYouTubeVideoPlayer(
                    videoPath = videoPath!!,
                    onRemoveVideo = { videoPath = null }
                )
            }

            if (audioPlaylist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                EmbeddedAudioPlaylistPlayer(
                    playlist = audioPlaylist,
                    isEditMode = true,
                    onMoveTrackUp = { idx ->
                        val mutable = audioPlaylist.toMutableList()
                        if (idx > 0) {
                            val moved = mutable.removeAt(idx)
                            mutable.add(idx - 1, moved)
                            audioPlaylist = mutable
                        }
                    },
                    onMoveTrackDown = { idx ->
                        val mutable = audioPlaylist.toMutableList()
                        if (idx < mutable.lastIndex) {
                            val moved = mutable.removeAt(idx)
                            mutable.add(idx + 1, moved)
                            audioPlaylist = mutable
                        }
                    },
                    onRemoveTrack = { trackId ->
                        audioPlaylist = audioPlaylist.filterNot { it.id == trackId }
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showFolderPicker) {
        MoveNoteFolderDialog(
            folders = folders,
            currentFolderId = folderId,
            onDismiss = { showFolderPicker = false },
            onSelectFolder = { picked ->
                folderId = picked
                showFolderPicker = false
            }
        )
    }
}

@Composable
private fun MoveNoteFolderDialog(
    folders: List<NoteFolder>,
    currentFolderId: String?,
    onDismiss: () -> Unit,
    onSelectFolder: (String?) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MindInnerSurface,
            title = {
                Text("نقل التدوينة إلى...", color = MindTextPrimary, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Option: No folder (General)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (currentFolderId == null) MindGoldAccent.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f))
                            .clickable { onSelectFolder(null) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.FolderOpen, contentDescription = null, tint = MindTextSecondary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("مساحة عامة (بدون مجلد)", color = MindTextPrimary, fontFamily = CairoFontFamily)
                    }

                    folders.forEach { folder ->
                        val isCurrent = folder.id == currentFolderId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) MindGoldAccent.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f))
                                .clickable { onSelectFolder(folder.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Folder, contentDescription = null, tint = MindGoldAccent)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(folder.name, color = MindTextPrimary, fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إغلاق", color = MindTextSecondary, fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

@Composable
private fun FolderNameDialog(
    title: String,
    initialName: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MindInnerSurface,
            title = {
                Text(title, color = MindTextPrimary, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("اسم المجلد...", color = MindTextSecondary, fontFamily = CairoFontFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MindGoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = MindTextPrimary,
                        unfocusedTextColor = MindTextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                    Text(confirmLabel, color = MindGoldAccent, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء", color = MindTextSecondary, fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

@Composable
private fun FolderManageDialog(
    folder: NoteFolder,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(folder.name) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MindInnerSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null, tint = MindGoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إدارة المجلد", color = MindTextPrimary, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MindGoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = MindTextPrimary,
                        unfocusedTextColor = MindTextPrimary
                    )
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDelete) {
                        Text("حذف المجلد", color = Color(0xFFE57373), fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { if (name.isNotBlank()) onRename(name) }) {
                        Text("حفظ الاسم", color = MindGoldAccent, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء", color = MindTextSecondary, fontFamily = CairoFontFamily)
                }
            }
        )
    }
}
