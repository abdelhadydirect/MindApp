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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.navigation.MindNavGraph
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

        setContent {
            val mindViewModel: MindViewModel = viewModel()
            val isLightTheme by mindViewModel.appLightTheme.collectAsState()

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
                            MindNavGraph(viewModel = mindViewModel)
                        }
                    }
                }
            }
        }
    }
}

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
                        MindCardTop.copy(alpha = 0.25f),
                        MindBgDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
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
