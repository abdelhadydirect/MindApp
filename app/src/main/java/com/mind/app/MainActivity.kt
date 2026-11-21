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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.navigation.MindNavGraph
import com.mind.app.ui.theme.MindTheme
import kotlinx.coroutines.delay
import kotlin.math.hypot

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
                        delay(3650L)
                        showSplash = false
                    }

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(tween(450, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(tween(400, easing = FastOutSlowInEasing))
                        },
                        label = "mind_quad_intro_to_main"
                    ) { isSplashing ->
                        if (isSplashing) {
                            MindFourFrameIntroScreen(targetThemeIsMint = isMintTheme)
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
 * 4-Frame Cinematic Intro ("M" -> "I" -> "N" -> "D" -> Dissolving into Light from Darkness)
 * Rendered at 60fps via native Compose Canvas with geometric transitions.
 */
@Composable
private fun MindFourFrameIntroScreen(targetThemeIsMint: Boolean) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3600, easing = LinearEasing)
        )
    }

    val p = progress.value
    val finalSurface = if (targetThemeIsMint) Color(0xFFE6F7F0) else Color(0xFFF6EEDB)

    // Core harmonized palette
    val nightEmerald = Color(0xFF091C16)
    val vibrantEmerald = Color(0xFF155D43)
    val joyfulMint = Color(0xFFCBEAD9)
    val warmIvory = Color(0xFFF6EEDB)
    val brushedGold = Color(0xFFE5B94C)
    val deepGold = Color(0xFFB88414)

    fun smooth(t: Float): Float {
        val x = t.coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val cy = h * 0.48f
            val letterH = h * 0.14f
            val strokeW = letterH * 0.15f

            // Phase timings:
            // Frame 1 (M): 0.00 -> 0.22
            // Transition 1->2 (Diagonal Wipe): 0.18 -> 0.24
            // Frame 2 (I): 0.22 -> 0.42
            // Transition 2->3 (Circular Iris): 0.38 -> 0.44
            // Frame 3 (N): 0.42 -> 0.62
            // Transition 3->4 (Vertical Split): 0.58 -> 0.64
            // Frame 4 (D + Full MIND Lockup): 0.62 -> 0.80
            // Finale (Darkness closes in -> MIND dissolves into Light): 0.80 -> 1.00

            when {
                // FRAME 1: 'M' on Night Emerald + Diagonal Sweep to Frame 2
                p < 0.22f -> {
                    drawRect(nightEmerald)
                    val mEnter = smooth(p / 0.12f)
                    scale(scale = 0.82f + 0.18f * mEnter, pivot = Offset(cx, cy)) {
                        drawLetterM(
                            center = Offset(cx, cy),
                            height = letterH,
                            strokeWidth = strokeW,
                            color = brushedGold,
                            alpha = mEnter
                        )
                    }

                    // Diagonal Wipe Transition to Frame 2
                    val wipe = smooth((p - 0.16f) / 0.06f)
                    if (wipe > 0f) {
                        val diagPath = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(w * wipe * 1.45f, 0f)
                            lineTo(w * (wipe * 1.45f - 0.45f), h)
                            lineTo(0f, h)
                            close()
                        }
                        clipPath(diagPath) {
                            drawRect(vibrantEmerald)
                            drawLetterI(
                                center = Offset(cx, cy),
                                height = letterH,
                                strokeWidth = strokeW,
                                color = warmIvory,
                                dotColor = brushedGold,
                                alpha = wipe
                            )
                        }
                    }
                }

                // FRAME 2: 'I' on Vibrant Emerald + Circular Iris Burst to Frame 3
                p < 0.42f -> {
                    drawRect(vibrantEmerald)
                    val iPulse = smooth((p - 0.22f) / 0.10f)
                    scale(scale = 0.92f + 0.08f * iPulse, pivot = Offset(cx, cy)) {
                        drawLetterI(
                            center = Offset(cx, cy),
                            height = letterH,
                            strokeWidth = strokeW,
                            color = warmIvory,
                            dotColor = brushedGold,
                            alpha = 1f
                        )
                    }

                    // Circular Iris Expansion into Frame 3
                    val iris = smooth((p - 0.36f) / 0.06f)
                    if (iris > 0f) {
                        val maxR = hypot(w, h) * 0.65f
                        val irisPath = Path().apply {
                            addOval(Rect(center = Offset(cx, cy), radius = maxR * iris))
                        }
                        clipPath(irisPath) {
                            drawRect(joyfulMint)
                            drawLetterN(
                                center = Offset(cx, cy),
                                height = letterH,
                                strokeWidth = strokeW,
                                color = nightEmerald,
                                alpha = iris
                            )
                        }
                    }
                }

                // FRAME 3: 'N' on Joyful Mint + Horizontal Curtain Split to Frame 4
                p < 0.62f -> {
                    drawRect(joyfulMint)
                    val nEnter = smooth((p - 0.42f) / 0.10f)
                    scale(scale = 0.90f + 0.10f * nEnter, pivot = Offset(cx, cy)) {
                        drawLetterN(
                            center = Offset(cx, cy),
                            height = letterH,
                            strokeWidth = strokeW,
                            color = nightEmerald,
                            alpha = 1f
                        )
                    }

                    // Center-Out Geometric Curtain Split into Frame 4
                    val split = smooth((p - 0.56f) / 0.06f)
                    if (split > 0f) {
                        val halfW = (w * 0.5f) * split
                        val splitPath = Path().apply {
                            addRect(Rect(cx - halfW, 0f, cx + halfW, h))
                        }
                        clipPath(splitPath) {
                            drawRect(warmIvory)
                            drawLetterD(
                                center = Offset(cx, cy),
                                height = letterH,
                                strokeWidth = strokeW,
                                color = deepGold,
                                alpha = split
                            )
                        }
                    }
                }

                // FRAME 4: 'D' converges with 'M I N' into full 'MIND' wordmark
                p < 0.78f -> {
                    drawRect(warmIvory)
                    val assemble = smooth((p - 0.62f) / 0.10f)
                    drawFullMindWordmark(
                        center = Offset(cx, cy),
                        spreadFactor = 1f - assemble,
                        mColor = nightEmerald,
                        iColor = vibrantEmerald,
                        nColor = nightEmerald,
                        dColor = deepGold,
                        alpha = 1f
                    )
                }

                // FINALE (0.78 -> 1.00): Darkness encloses MIND, then MIND dissolves into radiant Light!
                else -> {
                    val fin = ((p - 0.78f) / 0.22f).coerceIn(0f, 1f)
                    val darkPhase = smooth((fin / 0.42f).coerceIn(0f, 1f))
                    val lightBurst = smooth(((fin - 0.38f) / 0.62f).coerceIn(0f, 1f))

                    // Background shifts into deep night darkness before the light emerges
                    val bg = lerp(warmIvory, nightEmerald, darkPhase)
                    drawRect(bg)

                    // MIND glows intensely in gold, then dissolves as the light beam erupts
                    val wordAlpha = (1f - smooth(((fin - 0.35f) / 0.35f).coerceIn(0f, 1f)))
                    if (wordAlpha > 0.01f) {
                        val glowColor = lerp(nightEmerald, brushedGold, darkPhase)
                        scale(scale = 1f + 0.12f * lightBurst, pivot = Offset(cx, cy)) {
                            drawFullMindWordmark(
                                center = Offset(cx, cy),
                                spreadFactor = 0f,
                                mColor = glowColor,
                                iColor = brushedGold,
                                nColor = glowColor,
                                dColor = brushedGold,
                                alpha = wordAlpha
                            )
                        }
                    }

                    // Radiant Light emerging from the dark center
                    if (lightBurst > 0f) {
                        val maxBurstR = hypot(w, h) * 0.85f
                        val r = maxBurstR * lightBurst
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFFFE9A6),
                                    brushedGold.copy(alpha = 0.7f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = r.coerceAtLeast(1f)
                            ),
                            radius = r,
                            center = Offset(cx, cy)
                        )

                        // Final smooth wash into the app's active theme surface
                        val washAlpha = smooth(((lightBurst - 0.45f) / 0.55f).coerceIn(0f, 1f))
                        if (washAlpha > 0f) {
                            drawRect(finalSurface.copy(alpha = washAlpha))
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawLetterM(
    center: Offset,
    height: Float,
    strokeWidth: Float,
    color: Color,
    alpha: Float
) {
    val halfW = height * 0.46f
    val halfH = height * 0.5f
    val path = Path().apply {
        moveTo(center.x - halfW, center.y + halfH)
        lineTo(center.x - halfW, center.y - halfH)
        lineTo(center.x, center.y + halfH * 0.15f)
        lineTo(center.x + halfW, center.y - halfH)
        lineTo(center.x + halfW, center.y + halfH)
    }
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawLetterI(
    center: Offset,
    height: Float,
    strokeWidth: Float,
    color: Color,
    dotColor: Color,
    alpha: Float
) {
    val halfH = height * 0.5f
    drawLine(
        color = color.copy(alpha = alpha),
        start = Offset(center.x, center.y - halfH * 0.55f),
        end = Offset(center.x, center.y + halfH),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = dotColor.copy(alpha = alpha),
        radius = strokeWidth * 0.62f,
        center = Offset(center.x, center.y - halfH * 0.92f)
    )
}

private fun DrawScope.drawLetterN(
    center: Offset,
    height: Float,
    strokeWidth: Float,
    color: Color,
    alpha: Float
) {
    val halfW = height * 0.38f
    val halfH = height * 0.5f
    val path = Path().apply {
        moveTo(center.x - halfW, center.y + halfH)
        lineTo(center.x - halfW, center.y - halfH)
        lineTo(center.x + halfW, center.y + halfH)
        lineTo(center.x + halfW, center.y - halfH)
    }
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawLetterD(
    center: Offset,
    height: Float,
    strokeWidth: Float,
    color: Color,
    alpha: Float
) {
    val halfW = height * 0.38f
    val halfH = height * 0.5f
    val path = Path().apply {
        moveTo(center.x - halfW, center.y - halfH)
        lineTo(center.x - halfW, center.y + halfH)
        lineTo(center.x - halfW * 0.15f, center.y + halfH)
        cubicTo(
            center.x + halfW * 1.15f, center.y + halfH,
            center.x + halfW * 1.15f, center.y - halfH,
            center.x - halfW * 0.15f, center.y - halfH
        )
        close()
    }
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawFullMindWordmark(
    center: Offset,
    spreadFactor: Float,
    mColor: Color,
    iColor: Color,
    nColor: Color,
    dColor: Color,
    alpha: Float
) {
    val h = size.height * 0.085f
    val sw = h * 0.16f
    val spacing = h * 1.05f
    val extraSpread = size.width * 0.22f * spreadFactor

    // M - I - N - D (Left to Right visual order)
    drawLetterM(
        center = Offset(center.x - spacing * 1.55f - extraSpread, center.y),
        height = h,
        strokeWidth = sw,
        color = mColor,
        alpha = alpha * (1f - spreadFactor * 0.4f)
    )
    drawLetterI(
        center = Offset(center.x - spacing * 0.52f - extraSpread * 0.4f, center.y),
        height = h,
        strokeWidth = sw,
        color = iColor,
        dotColor = dColor,
        alpha = alpha * (1f - spreadFactor * 0.4f)
    )
    drawLetterN(
        center = Offset(center.x + spacing * 0.42f + extraSpread * 0.4f, center.y),
        height = h,
        strokeWidth = sw,
        color = nColor,
        alpha = alpha * (1f - spreadFactor * 0.4f)
    )
    drawLetterD(
        center = Offset(center.x + spacing * 1.48f + extraSpread, center.y),
        height = h,
        strokeWidth = sw,
        color = dColor,
        alpha = alpha
    )
}
