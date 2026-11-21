package com.mind.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.app.R
import com.mind.app.domain.model.PrayerDashboardState
import com.mind.app.domain.model.QuoteItem
import com.mind.app.domain.model.SinglePrayer
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindCardBottom
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary
import com.mind.app.ui.theme.QuranFontFamily
import com.mind.app.ui.theme.isAppLightTheme

@Composable
fun MindTopBar(
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSettingsClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MindTextPrimary,
                        modifier = Modifier.size(25.dp)
                    )
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleMusic()
                    }
                ) {
                    Icon(
                        imageVector = if (isMusicPlaying) {
                            Icons.AutoMirrored.Outlined.VolumeUp
                        } else {
                            Icons.AutoMirrored.Outlined.VolumeOff
                        },
                        contentDescription = "Toggle Ambient Music",
                        tint = if (isMusicPlaying) MindGoldAccent else MindTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mind",
                    color = MindTextPrimary,
                    fontFamily = CairoFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mind_brain_header),
                        contentDescription = "Mind Glowing Brain Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun QuranVerseHeader(verse: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "﴿ $verse ﴾",
            color = MindTextPrimary,
            fontFamily = QuranFontFamily,
            fontSize = 27.sp,
            textAlign = TextAlign.Center,
            lineHeight = 46.sp
        )
    }
}

@Composable
fun PrayerDashboardCard(state: PrayerDashboardState) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "prayer_progress"
    )

    val cardBorderColor = if (isAppLightTheme) {
        MindGoldAccent.copy(alpha = 0.35f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    val mosqueCircleBg = if (isAppLightTheme) {
        MindInnerSurface.copy(alpha = 0.9f)
    } else {
        Color.White.copy(alpha = 0.14f)
    }

    val innerBoxBg = if (isAppLightTheme) {
        MindInnerSurface.copy(alpha = 0.96f)
    } else {
        MindInnerSurface.copy(alpha = 0.72f)
    }

    val trackBg = if (isAppLightTheme) {
        MindMintIcon.copy(alpha = 0.16f)
    } else {
        Color.White.copy(alpha = 0.14f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MindCardTop, MindCardBottom)
                )
            )
            .border(1.dp, cardBorderColor, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(mosqueCircleBg)
                        .border(1.dp, cardBorderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mosque,
                        contentDescription = null,
                        tint = if (isAppLightTheme) MindMintIcon else MindTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = state.dayNameAr,
                        color = MindTextPrimary,
                        fontFamily = CairoFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.hijriDateAr,
                        color = MindGoldAccent,
                        fontFamily = CairoFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${state.gregorianDateAr}  •",
                        color = MindTextSecondary,
                        fontFamily = CairoFontFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(innerBoxBg)
                    .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "متبقي على الأذان",
                            color = MindTextSecondary,
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.remainingHoursMinutes,
                            color = MindTextPrimary,
                            fontFamily = CairoFontFamily,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = "الصلاة القادمة: ${state.nextPrayerName}",
                                color = MindGoldAccent,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = state.nextPrayerTimeFormatted,
                                color = MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(trackBg),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(MindGoldAccent)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.remainingText,
                        color = MindTextSecondary,
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp
                    )
                    Text(
                        text = state.elapsedText,
                        color = MindTextSecondary,
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(state.prayersList, key = { it.nameAr }) { prayer ->
                    SinglePrayerChip(prayer = prayer)
                }
            }
        }
    }
}

@Composable
private fun SinglePrayerChip(prayer: SinglePrayer) {
    val borderColor = if (prayer.isNext) {
        MindGoldAccent
    } else if (isAppLightTheme) {
        MindGoldAccent.copy(alpha = 0.22f)
    } else {
        Color.Transparent
    }

    val bgColor = if (prayer.isNext) {
        MindInnerSurface
    } else {
        MindInnerSurface.copy(alpha = if (isAppLightTheme) 0.85f else 0.55f)
    }

    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (prayer.isNext) 1.5.dp else if (isAppLightTheme) 1.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prayer.nameAr,
            color = if (prayer.isNext) MindGoldAccent else MindTextSecondary,
            fontFamily = CairoFontFamily,
            fontSize = 14.sp,
            fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = prayer.timeFormatted,
            color = MindTextPrimary,
            fontFamily = CairoFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
fun DynamicQuoteSpace(
    quote: QuoteItem?,
    onQuoteTap: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = quote != null,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(450)),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(250))
    ) {
        quote?.let { currentQuote ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onQuoteTap()
                    }
                    .padding(horizontal = 28.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentQuote,
                    transitionSpec = {
                        (fadeIn(tween(400, easing = FastOutSlowInEasing)) +
                            slideInVertically(tween(400)) { it / 4 }) togetherWith
                            (fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it / 4 })
                    },
                    label = "quote_text_transition"
                ) { targetQuote ->
                    Text(
                        text = targetQuote.text,
                        color = MindGoldAccent,
                        fontFamily = CairoFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalBottomActionBar(
    onQuranClick: () -> Unit,
    onNotesClick: () -> Unit,
    onTasksClick: () -> Unit,
    onNutritionClick: () -> Unit = {},
    onChannelsClick: () -> Unit = {},
    onQuotesEditorClick: () -> Unit
) {
    val navBorderColor = if (isAppLightTheme) {
        MindGoldAccent.copy(alpha = 0.32f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(CircleShape)
                .background(MindInnerSurface.copy(alpha = 0.94f))
                .border(1.dp, navBorderColor, CircleShape)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpringIconAction(
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                contentDescription = "Quran",
                tint = MindTextPrimary,
                onClick = onQuranClick
            )
            SpringIconAction(
                icon = Icons.Outlined.Edit,
                contentDescription = "Notes",
                tint = MindTextPrimary,
                onClick = onNotesClick
            )
            SpringIconAction(
                icon = Icons.Outlined.CheckCircle,
                contentDescription = "Tasks",
                tint = MindTextPrimary,
                onClick = onTasksClick
            )
            SpringIconAction(
                icon = Icons.Outlined.FitnessCenter,
                contentDescription = "Nutrition",
                tint = MindTextPrimary,
                onClick = onNutritionClick
            )
            SpringIconAction(
                icon = Icons.Outlined.Explore,
                contentDescription = "Channels",
                tint = MindTextPrimary,
                onClick = onChannelsClick
            )
            SpringIconAction(
                icon = Icons.Outlined.Psychology,
                contentDescription = "Mind Quotes",
                tint = MindTextPrimary,
                onClick = onQuotesEditorClick
            )
        }
    }
}

@Composable
private fun SpringIconAction(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = MindTextPrimary,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.84f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_spring"
    )

    Box(
        modifier = Modifier
            .size(42.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(23.dp)
        )
    }
}
