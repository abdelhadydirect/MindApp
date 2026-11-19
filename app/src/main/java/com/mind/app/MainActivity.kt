package com.mind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.screens.HomeScreen
import com.mind.app.ui.screens.NotesScreen
import com.mind.app.ui.screens.QuotesEditorScreen
import com.mind.app.ui.screens.QuranScreen
import com.mind.app.ui.screens.SettingsScreen
import com.mind.app.ui.screens.TasksScreen
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            MindTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var showSplash by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        delay(1450L)
                        showSplash = false
                    }

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(tween(550, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(tween(450, easing = FastOutSlowInEasing))
                        },
                        label = "splash_to_main"
                    ) { isSplashing ->
                        if (isSplashing) {
                            MindWelcomeSplashScreen()
                        } else {
                            val navController = rememberNavController()
                            val mindViewModel: MindViewModel = viewModel()

                            NavHost(
                                navController = navController,
                                startDestination = "home"
                            ) {
                                composable("home") {
                                    HomeScreen(
                                        viewModel = mindViewModel,
                                        onNavigateToQuran = { navController.navigate("quran") },
                                        onNavigateToNotes = { navController.navigate("notes") },
                                        onNavigateToTasks = { navController.navigate("tasks") },
                                        onNavigateToQuotes = { navController.navigate("quotes") },
                                        onNavigateToSettings = { navController.navigate("settings") }
                                    )
                                }
                                composable("quran") {
                                    QuranScreen(
                                        viewModel = mindViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("notes") {
                                    NotesScreen(
                                        viewModel = mindViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("tasks") {
                                    TasksScreen(
                                        viewModel = mindViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("quotes") {
                                    QuotesEditorScreen(
                                        viewModel = mindViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("settings") {
                                    SettingsScreen(
                                        viewModel = mindViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Elevated Welcome Splash Screen derived from Mind's emerald & gold palette
 */
@Composable
private fun MindWelcomeSplashScreen() {
    var startAnim by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnim = true
    }

    val iconScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.78f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_icon_scale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(750, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "splash_text_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MindBgDark,
                        MindCardTop.copy(alpha = 0.32f),
                        MindBgDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Soft Golden Ambient Halo behind App Icon
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(iconScale)
                .blur(45.dp)
                .clip(CircleShape)
                .background(MindGoldAccent.copy(alpha = 0.16f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(134.dp)
                    .scale(iconScale)
                    .clip(RoundedCornerShape(34.dp))
                    .border(1.5.dp, MindGoldAccent.copy(alpha = 0.45f), RoundedCornerShape(34.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mind_launcher),
                    contentDescription = "Mind App Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Mind",
                color = MindTextPrimary,
                fontFamily = CairoFontFamily,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "صفاء الذهن.. وبركة الوقت",
                color = MindGoldAccent.copy(alpha = 0.9f),
                fontFamily = CairoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}
