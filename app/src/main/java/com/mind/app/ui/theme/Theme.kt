package com.mind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mind.app.R

// false = Warm Ivory Daylight (Default), true = Joyful Fresh Mint Theme
var isMintThemeMode by mutableStateOf(false)
val isAppLightTheme: Boolean get() = true

val MindBgDark: Color
    get() = if (isMintThemeMode) Color(0xFFE6F7F0) else Color(0xFFF6EEDB)

val MindCardTop: Color
    get() = if (isMintThemeMode) Color(0xFFBCEAD5) else Color(0xFFDFECE3)

val MindCardBottom: Color
    get() = if (isMintThemeMode) Color(0xFFA3DFC5) else Color(0xFFCFE0D5)

val MindInnerSurface: Color
    get() = if (isMintThemeMode) Color(0xFFF4FCF8) else Color(0xFFFAF6EE)

val MindGoldAccent: Color
    get() = if (isMintThemeMode) Color(0xFFB88414) else Color(0xFFA67714)

val MindTextPrimary: Color
    get() = if (isMintThemeMode) Color(0xFF0E4230) else Color(0xFF133A2B)

val MindTextSecondary: Color
    get() = if (isMintThemeMode) Color(0xFF3E6E5C) else Color(0xFF4F6B5D)

val MindMintIcon: Color
    get() = if (isMintThemeMode) Color(0xFF127A56) else Color(0xFF155D43)

@OptIn(ExperimentalTextApi::class)
val CairoFontFamily = FontFamily(
    Font(
        resId = R.font.cairo,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.cairo,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.cairo,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.cairo,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    )
)

val QuranFontFamily = FontFamily(
    Font(resId = R.font.amiri_quran, weight = FontWeight.Normal)
)

val MindTypography = Typography(
    displayLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold),
    displayMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold),
    displaySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 11.sp)
)

private val WarmIvoryColorScheme = lightColorScheme(
    primary = Color(0xFF155D43),
    secondary = Color(0xFFA67714),
    background = Color(0xFFF6EEDB),
    surface = Color(0xFFFAF6EE),
    onPrimary = Color(0xFFF6EEDB),
    onSecondary = Color(0xFFF6EEDB),
    onBackground = Color(0xFF133A2B),
    onSurface = Color(0xFF133A2B)
)

private val JoyfulMintColorScheme = lightColorScheme(
    primary = Color(0xFF127A56),
    secondary = Color(0xFFB88414),
    background = Color(0xFFE6F7F0),
    surface = Color(0xFFF4FCF8),
    onPrimary = Color(0xFFF4FCF8),
    onSecondary = Color(0xFFF4FCF8),
    onBackground = Color(0xFF0E4230),
    onSurface = Color(0xFF0E4230)
)

@Composable
fun MindTheme(
    mintTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    isMintThemeMode = mintTheme
    val scheme = if (mintTheme) JoyfulMintColorScheme else WarmIvoryColorScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = MindTypography,
        content = content
    )
}
