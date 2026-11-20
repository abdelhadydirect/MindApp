package com.mind.app

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.navigation.MindNavGraph
import com.mind.app.ui.theme.MindTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val mindViewModel: MindViewModel = viewModel()
            val isLightTheme by mindViewModel.appLightTheme.collectAsState()
            val isMusicEnabled by mindViewModel.ambientMusicEnabled.collectAsState()

            // Continuous Ambient Music Player (Starts with Dawn Intro, continues smoothly in App)
            val mediaPlayer = remember {
                runCatching {
                    MediaPlayer.create(context, R.raw.mind_ambient)?.apply {
                        isLooping = true
                        setVolume(0.75f, 0.75f)
                    }
                }.getOrNull()
            }

            LaunchedEffect(isMusicEnabled) {
                mediaPlayer?.let { mp ->
                    if (isMusicEnabled) {
                        if (!mp.isPlaying) runCatching { mp.start() }
                    } else {
                        if (mp.isPlaying) runCatching { mp.pause() }
                    }
                }
            }

            DisposableEffect(lifecycleOwner, isMusicEnabled) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            if (isMusicEnabled && mediaPlayer?.isPlaying == false) {
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

            LaunchedEffect(isLightTheme) {
                val barStyle = if (isLightTheme) {
                    SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
            }

            MindTheme(lightTheme = isLightTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var showSplash by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        delay(3600L)
                        showSplash = false
                    }

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(tween(600, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(tween(500, easing = FastOutSlowInEasing))
                        },
                        label = "dawn_intro_to_main"
                    ) { isSplashing ->
                        if (isSplashing) {
                            FirstLightOfDawnIntroScreen()
                        } else {
                            MindNavGraph(viewModel = mindViewModel)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun FirstLightOfDawnIntroScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF091C16))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.parseColor("#091C16"))
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    loadUrl("file:///android_asset/dawn_intro.html")
                }
            }
        )
    }
}
