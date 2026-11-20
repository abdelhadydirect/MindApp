package com.mind.app

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.navigation.MindNavGraph
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val mindViewModel: MindViewModel = viewModel()
            val isMintTheme by mindViewModel.mintThemeEnabled.collectAsState()
            val shouldPlayMusic by mindViewModel.ambientMusicEnabled.collectAsState()

            val mediaPlayer = remember {
                runCatching {
                    MediaPlayer.create(context, R.raw.mind_ambient)?.apply {
                        isLooping = true
                        setVolume(0.75f, 0.75f)
                    }
                }.getOrNull()
            }

            LaunchedEffect(shouldPlayMusic) {
                mediaPlayer?.let { mp ->
                    if (shouldPlayMusic) {
                        if (!mp.isPlaying) runCatching { mp.start() }
                    } else {
                        if (mp.isPlaying) runCatching { mp.pause() }
                    }
                }
            }

            DisposableEffect(lifecycleOwner, shouldPlayMusic) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            if (shouldPlayMusic && mediaPlayer?.isPlaying == false) {
                                runCatching { mediaPlayer.start() }
                            }
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            if (mediaPlayer?.isPlaying == true) {
                                runCatching { mediaPlayer.pause() }
                            }
                        }
                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    runCatching {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                    }
                }
            }

            LaunchedEffect(Unit) {
                val barStyle = SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
            }

            MindTheme(mintTheme = isMintTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var showSplash by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        delay(3300L)
                        showSplash = false
                    }

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(tween(550, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(tween(450, easing = FastOutSlowInEasing))
                        },
                        label = "dawn_intro_to_main"
                    ) { isSplashing ->
                        if (isSplashing) {
                            NativeFirstLightOfDawnScreen()
                        } else {
                            MindNavGraph(viewModel = mindViewModel)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 100% Native 60fps Jetpack Compose Canvas animation for "First Light of Dawn — انفراج الفجر"
 * Zero latency, zero gray screen, seamless transition from night emerald to morning ivory.
 */
@Composable
private fun NativeFirstLightOfDawnScreen() {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3100, easing = LinearEasing)
        )
    }

    val stars = remember {
        val rnd = Random(7)
        List(75) {
            Triple(rnd.nextFloat(), rnd.nextFloat() * 0.65f, 1.2f + rnd.nextFloat() * 2.2f)
        }
    }

    val p = progress.value
    val dawnProgress = ((p - 0.15f) / 0.55f).coerceIn(0f, 1f)
    val smoothDawn = dawnProgress * dawnProgress * (3f - 2f * dawnProgress)
    val textAlpha = ((p - 0.42f) / 0.28f).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val horizY = h * 0.84f

            // Sky gradient from Night Emerald (#091C16) -> Teal (#19664B) -> Warm Ivory (#F6EEDB)
            val nightTop = Color(0xFF091C16)
            val midTeal = Color(0xFF19664B)
            val morningIvory = Color(0xFFF6EEDB)
            val goldenHorizon = Color(0xFFE5B94C)

            val skyTop = lerp(nightTop, morningIvory, smoothDawn * 0.92f)
            val skyMid = lerp(Color(0xFF0E3F2E), Color(0xFFF3E2B8), smoothDawn)
            val skyHor = lerp(Color(0xFF155D43), goldenHorizon, (smoothDawn * 1.2f).coerceIn(0f, 1f) * 0.65f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(skyTop, skyMid, skyHor, morningIvory)
                )
            )

            // Twinkling Stars (fade out as dawn breaks)
            val starAlpha = (1f - smoothDawn * 1.6f).coerceIn(0f, 1f)
            if (starAlpha > 0.01f) {
                stars.forEachIndexed { idx, (sx, sy, sr) ->
                    val twinkle = 0.55f + 0.45f * sin(p * 18f + idx).toFloat()
                    drawCircle(
                        color = Color(0xFFFFF4D6).copy(alpha = starAlpha * twinkle * 0.85f),
                        radius = sr,
                        center = Offset(sx * w, sy * h)
                    )
                }
            }

            // Golden Crescent Moon in upper sky
            if (starAlpha > 0.01f) {
                val moonCenter = Offset(w * 0.74f, h * 0.16f)
                val moonR = w * 0.065f
                drawCircle(
                    color = Color(0xFFE5B94C).copy(alpha = starAlpha * 0.9f),
                    radius = moonR,
                    center = moonCenter
                )
                drawCircle(
                    color = skyTop.copy(alpha = starAlpha),
                    radius = moonR * 0.85f,
                    center = Offset(moonCenter.x - moonR * 0.35f, moonCenter.y - moonR * 0.15f)
                )
            }

            // Expanding Golden Dawn Sunburst at Horizon
            val glowRadius = w * (0.35f + 0.85f * smoothDawn)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE599).copy(alpha = 0.85f * smoothDawn),
                        Color(0xFFE5B94C).copy(alpha = 0.35f * smoothDawn),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, horizY),
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = Offset(w * 0.5f, horizY)
            )

            // First Thread of Dawn Horizontal Beam
            val lineHalf = w * 0.48f * ((p - 0.12f) / 0.3f).coerceIn(0f, 1f)
            if (lineHalf > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFFFE8A3), Color.Transparent),
                        startX = w * 0.5f - lineHalf,
                        endX = w * 0.5f + lineHalf
                    ),
                    topLeft = Offset(w * 0.5f - lineHalf, horizY - 3f),
                    size = Size(lineHalf * 2f, 6f)
                )
            }

            // Far Rolling Hills
            val hillColor = lerp(Color(0xFF0A241B), Color(0xFF6B9E85), smoothDawn)
            val hillPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, horizY)
                quadraticTo(w * 0.25f, horizY - 35f, w * 0.55f, horizY + 5f)
                quadraticTo(w * 0.82f, horizY + 25f, w, horizY - 20f)
                lineTo(w, h)
                close()
            }
            drawPath(hillPath, color = hillColor)

            // Mosque Dome & Minarets Silhouette
            val mosqueColor = lerp(Color(0xFF071B14), Color(0xFF2E6B52), smoothDawn)
            val mx = w * 0.28f
            val baseW = w * 0.16f
            drawRect(
                color = mosqueColor,
                topLeft = Offset(mx - baseW / 2f, horizY - 32f),
                size = Size(baseW, 45f)
            )
            drawArc(
                color = mosqueColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(mx - baseW * 0.34f, horizY - 68f),
                size = Size(baseW * 0.68f, 72f)
            )
            // Twin Minarets
            for (side in listOf(-1f, 1f)) {
                val minX = mx + side * (baseW * 0.62f)
                drawRect(
                    color = mosqueColor,
                    topLeft = Offset(minX - 5f, horizY - 92f),
                    size = Size(10f, 105f)
                )
            }

            // Foreground Emerald Ridge
            val nearColor = lerp(Color(0xFF05130E), Color(0xFF155D43), smoothDawn)
            val nearPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.91f)
                quadraticTo(w * 0.5f, h * 0.86f, w, h * 0.92f)
                lineTo(w, h)
                close()
            }
            drawPath(nearPath, color = nearColor)
        }

        // Brand Reveal ("Mind" + "صفاء الذهن.. وبركة الوقت")
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 90.dp)
                .alpha(textAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mind",
                color = Color(0xFF091C16),
                fontFamily = CairoFontFamily,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "صفاء الذهن.. وبركة الوقت",
                color = Color(0xFF155D43),
                fontFamily = CairoFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
