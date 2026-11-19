package com.mind.app.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.mind.app.domain.model.AudioTrackItem
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindCardBottom
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale

private fun formatDurationMs(ms: Long): String {
    val totalSecs = (ms.coerceAtLeast(0L) / 1000L).toInt()
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}

/**
 * YouTube-Style Embedded Video Player with custom overlay controls
 */
@OptIn(UnstableApi::class)
@Composable
fun EmbeddedYouTubeVideoPlayer(
    videoPath: String,
    onRemoveVideo: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val exoPlayer = remember(videoPath) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(File(videoPath))))
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var currentPos by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPos = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(0L)
            delay(250L)
        }
    }

    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3200L)
            showControls = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                .clickable { showControls = !showControls }
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = showControls || !isPlaying,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(12.dp)
                ) {
                    // Top bar (Mute & Optional Remove)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                isMuted = !isMuted
                                exoPlayer.volume = if (isMuted) 0f else 1f
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
                                contentDescription = "Mute",
                                tint = MindTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (onRemoveVideo != null) {
                            IconButton(
                                onClick = onRemoveVideo,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Remove Video",
                                    tint = MindTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Center YouTube Controls (-10s, Play/Pause, +10s)
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                            currentPos = exoPlayer.currentPosition
                        }) {
                            Icon(Icons.Outlined.Replay10, contentDescription = "-10s", tint = Color.White, modifier = Modifier.size(30.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MindGoldAccent)
                                .clickable {
                                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = MindBgDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(onClick = {
                            exoPlayer.seekTo((exoPlayer.currentPosition + 10_000L).coerceAtMost(duration))
                            currentPos = exoPlayer.currentPosition
                        }) {
                            Icon(Icons.Outlined.Forward10, contentDescription = "+10s", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }

                    // Bottom Scrubber & Timestamps
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Slider(
                            value = if (duration > 0) (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f,
                            onValueChange = { fraction ->
                                val target = (fraction * duration).toLong()
                                currentPos = target
                                exoPlayer.seekTo(target)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MindGoldAccent,
                                activeTrackColor = MindGoldAccent,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatDurationMs(currentPos), color = Color.White, fontSize = 11.sp)
                            Text(formatDurationMs(duration), color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * YouTube-Style Embedded Audio & Smart Playlist Player
 * Supports single track or multi-track playlist with manual reordering & auto-advance.
 */
@Composable
fun EmbeddedAudioPlaylistPlayer(
    playlist: List<AudioTrackItem>,
    isEditMode: Boolean = false,
    onMoveTrackUp: ((Int) -> Unit)? = null,
    onMoveTrackDown: ((Int) -> Unit)? = null,
    onRemoveTrack: ((String) -> Unit)? = null
) {
    if (playlist.isEmpty()) return

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(false) }
    var currentPos by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    // Sync playlist items into ExoPlayer whenever order changes
    LaunchedEffect(playlist) {
        val safeIdx = currentIndex.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))
        currentIndex = safeIdx
        val wasPlaying = exoPlayer.isPlaying
        val mediaItems = playlist.map { MediaItem.fromUri(Uri.fromFile(File(it.filePath))) }
        exoPlayer.setMediaItems(mediaItems, safeIdx, exoPlayer.currentPosition)
        exoPlayer.prepare()
        if (wasPlaying) exoPlayer.play()
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentIndex = exoPlayer.currentMediaItemIndex.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))
                currentPos = 0L
                duration = exoPlayer.duration.coerceAtLeast(0L)
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPos = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(0L)
            delay(200L)
        }
    }

    val activeTrack = playlist.getOrNull(currentIndex) ?: playlist.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MindCardTop.copy(alpha = 0.85f), MindInnerSurface)
                )
            )
            .border(1.dp, MindGoldAccent.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        // Now Playing Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MindGoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GraphicEq,
                        contentDescription = null,
                        tint = MindGoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = activeTrack.title,
                        color = MindTextPrimary,
                        fontFamily = CairoFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (playlist.size > 1) {
                        Text(
                            text = "مقطع ${currentIndex + 1} من ${playlist.size} • قائمة تشغيل",
                            color = MindGoldAccent,
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    isLooping = !isLooping
                    exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Repeat,
                    contentDescription = "Loop",
                    tint = if (isLooping) MindGoldAccent else MindTextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // YouTube-style Scrubber (LTR for accurate time progression)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column {
                Slider(
                    value = if (duration > 0) (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f,
                    onValueChange = { fraction ->
                        val target = (fraction * duration).toLong()
                        currentPos = target
                        exoPlayer.seekTo(target)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MindGoldAccent,
                        activeTrackColor = MindGoldAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(26.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDurationMs(currentPos), color = MindTextSecondary, fontSize = 11.sp)
                    Text(formatDurationMs(duration), color = MindTextSecondary, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Transport Controls: Prev, -10s, Play/Pause, +10s, Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (exoPlayer.hasPreviousMediaItem()) exoPlayer.seekToPreviousMediaItem()
                            else exoPlayer.seekTo(0L)
                        }
                    ) {
                        Icon(Icons.Outlined.SkipPrevious, contentDescription = "Previous", tint = MindTextPrimary)
                    }

                    IconButton(
                        onClick = {
                            exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                            currentPos = exoPlayer.currentPosition
                        }
                    ) {
                        Icon(Icons.Outlined.Replay10, contentDescription = "-10s", tint = MindTextPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MindGoldAccent)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = MindBgDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            exoPlayer.seekTo((exoPlayer.currentPosition + 10_000L).coerceAtMost(duration))
                            currentPos = exoPlayer.currentPosition
                        }
                    ) {
                        Icon(Icons.Outlined.Forward10, contentDescription = "+10s", tint = MindTextPrimary)
                    }

                    IconButton(
                        onClick = {
                            if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNextMediaItem()
                        },
                        enabled = playlist.size > 1
                    ) {
                        Icon(
                            Icons.Outlined.SkipNext,
                            contentDescription = "Next",
                            tint = if (playlist.size > 1) MindTextPrimary else MindTextSecondary.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // Playlist Queue with Manual Reordering Controls
        if (playlist.size > 1 || isEditMode) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                playlist.forEachIndexed { index, track ->
                    val isSelected = index == currentIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.98f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "track_scale"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MindGoldAccent.copy(alpha = 0.16f)
                                else Color.Black.copy(alpha = 0.2f)
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentIndex = index
                                exoPlayer.seekTo(index, 0L)
                                exoPlayer.play()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}.",
                                color = if (isSelected) MindGoldAccent else MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = track.title,
                                color = if (isSelected) MindGoldAccent else MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Manual Order Controls (Up / Down) & Delete
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onMoveTrackUp != null && index > 0) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onMoveTrackUp(index)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Move Up", tint = MindTextSecondary)
                                }
                            }
                            if (onMoveTrackDown != null && index < playlist.lastIndex) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onMoveTrackDown(index)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Move Down", tint = MindTextSecondary)
                                }
                            }
                            if (isEditMode && onRemoveTrack != null) {
                                IconButton(
                                    onClick = { onRemoveTrack(track.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Remove", tint = MindTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
