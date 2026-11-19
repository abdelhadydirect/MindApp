package com.mind.app.ui.alarm

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Snooze
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mind.app.alarm.MindAlarmScheduler
import com.mind.app.data.local.MindStorage
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmOverlayActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        val alarmType = intent.getStringExtra(MindAlarmScheduler.EXTRA_ALARM_TYPE) ?: MindAlarmScheduler.TYPE_ADHAN
        val prayerName = intent.getStringExtra(MindAlarmScheduler.EXTRA_PRAYER_NAME) ?: "الصلاة"
        val taskId = intent.getStringExtra(MindAlarmScheduler.EXTRA_TASK_ID)
        val taskTitle = intent.getStringExtra(MindAlarmScheduler.EXTRA_TASK_TITLE) ?: ""
        val taskImage = intent.getStringExtra(MindAlarmScheduler.EXTRA_TASK_IMAGE)

        val storage = MindStorage(this)

        CoroutineScope(Dispatchers.IO).launch {
            val savedUriStr = if (alarmType == MindAlarmScheduler.TYPE_ADHAN) {
                storage.getAdhanToneUriSync()
            } else {
                storage.getTaskToneUriSync()
            }
            val fallbackUri = RingtoneManager.getDefaultUri(
                if (alarmType == MindAlarmScheduler.TYPE_ADHAN) RingtoneManager.TYPE_NOTIFICATION
                else RingtoneManager.TYPE_ALARM
            )
            val toneUri = savedUriStr?.let { Uri.parse(it) } ?: fallbackUri
            startAlarmSound(toneUri, loop = (alarmType == MindAlarmScheduler.TYPE_TASK))
        }

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                if (alarmType == MindAlarmScheduler.TYPE_ADHAN) {
                    AdhanNeonScreen(
                        prayerName = prayerName,
                        onStopAdhan = {
                            stopAndFinish()
                        }
                    )
                } else {
                    TaskAlarmOverlayScreen(
                        taskTitle = taskTitle,
                        imagePath = taskImage,
                        onCompleteTask = {
                            if (taskId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val updated = storage.getTasksSync().map {
                                        if (it.id == taskId) it.copy(isDone = true) else it
                                    }
                                    storage.saveTasks(updated)
                                }
                            }
                            stopAndFinish()
                        },
                        onSnoozeTask = {
                            if (taskId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val tenMinsLater = System.currentTimeMillis() + 10 * 60 * 1000L
                                    val tasks = storage.getTasksSync()
                                    val updated = tasks.map {
                                        if (it.id == taskId) it.copy(dueAtMillis = tenMinsLater) else it
                                    }
                                    storage.saveTasks(updated)
                                    updated.find { it.id == taskId }?.let {
                                        MindAlarmScheduler.scheduleTaskAlarm(this@AlarmOverlayActivity, it)
                                    }
                                }
                            }
                            stopAndFinish()
                        }
                    )
                }
            }
        }
    }

    private fun startAlarmSound(uri: Uri, loop: Boolean) {
        runCatching {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmOverlayActivity, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = loop
                setOnCompletionListener {
                    if (!loop) stopAndFinish()
                }
                prepare()
                start()
            }
        }
    }

    private fun stopAndFinish() {
        runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        finish()
    }

    override fun onDestroy() {
        runCatching {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        super.onDestroy()
    }
}

/**
 * Pure OLED Black Adhan Screen with White Clock, Dynamic Phone-Colored Pulsing Neon Mosque,
 * Stop Adhan Button, and White "لا حول ولا قوة إلا بالله" at bottom.
 */
@Composable
private fun AdhanNeonScreen(
    prayerName: String,
    onStopAdhan: () -> Unit
) {
    val context = LocalContext.current
    // Extract dynamic system accent colors matching the phone's theme/wallpaper
    val neonPrimary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context).primary
    } else {
        Color(0xFF00E5FF)
    }
    val neonSecondary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context).tertiary
    } else {
        Color(0xFF7C4DFF)
    }

    var currentTimeText by remember {
        mutableStateOf(SimpleDateFormat("hh:mm", Locale.US).format(Date()))
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeText = SimpleDateFormat("hh:mm", Locale.US).format(Date())
            delay(5_000L)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "neon_mosque_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: White Clock + Prayer Name
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentTimeText,
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontSize = 68.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "أذان $prayerName",
                color = Color.White.copy(alpha = 0.88f),
                fontFamily = CairoFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Center: Pulsing Neon Mosque matching phone's dynamic colors
        Box(
            modifier = Modifier.size(210.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Neon Aura
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale)
                    .blur(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                neonPrimary.copy(alpha = glowAlpha),
                                neonSecondary.copy(alpha = glowAlpha * 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Neon Ring
            Box(
                modifier = Modifier
                    .size(146.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(listOf(neonPrimary, neonSecondary)),
                        shape = CircleShape
                    )
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mosque,
                    contentDescription = "Mosque",
                    tint = neonPrimary,
                    modifier = Modifier.size(74.dp)
                )
            }
        }

        // Bottom: Stop Adhan Button + White Dhikr ("لا حول ولا قوة إلا بالله")
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val btnScale by animateFloatAsState(
                targetValue = if (isPressed) 0.92f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "stop_btn_scale"
            )

            Row(
                modifier = Modifier
                    .scale(btnScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.5.dp, neonPrimary.copy(alpha = 0.8f), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onStopAdhan
                    )
                    .padding(horizontal = 32.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Stop,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إيقاف الأذان",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "لا حول ولا قوة إلا بالله",
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Elevated Task Reminder Overlay Screen (Shows over other apps with Complete / Snooze actions)
 */
@Composable
private fun TaskAlarmOverlayScreen(
    taskTitle: String,
    imagePath: String?,
    onCompleteTask: () -> Unit,
    onSnoozeTask: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "task_alarm_glow")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MindBgDark, MindCardTop.copy(alpha = 0.9f), MindBgDark)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(MindInnerSurface)
                .border(1.5.dp, MindGoldAccent.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(ringScale)
                    .clip(CircleShape)
                    .background(MindGoldAccent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Alarm,
                    contentDescription = null,
                    tint = MindGoldAccent,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "حان وقت المهمة",
                color = MindGoldAccent,
                fontFamily = CairoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = taskTitle,
                color = MindTextPrimary,
                fontFamily = CairoFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            if (imagePath != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = File(imagePath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Snooze Button (10 mins)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onSnoozeTask() }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Snooze, contentDescription = null, tint = MindTextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تأجيل ١٠د",
                        color = MindTextPrimary,
                        fontFamily = CairoFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Complete Task Button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MindMintIcon)
                        .clickable { onCompleteTask() }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = MindBgDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إنهاء المهمة",
                        color = MindBgDark,
                        fontFamily = CairoFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
