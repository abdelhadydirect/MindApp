package com.mind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
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

var isAppLightTheme by mutableStateOf(false)

val MindBgDark: Color
    get() = if (isAppLightTheme) Color(0xFFF6EEDB) else Color(0xFF091C16)

// Designer-Curated Soft Botanical Sage-Emerald Card for Warm Light Mode
val MindCardTop: Color
    get() = if (isAppLightTheme) Color(0xFFDFECE3) else Color(0xFF19664B)

val MindCardBottom: Color
    get() = if (isAppLightTheme) Color(0xFFCFE0D5) else Color(0xFF13523C)

// Warm Ivory-Sand Inner Surface for High-Contrast Harmony in Light Mode
val MindInnerSurface: Color
    get() = if (isAppLightTheme) Color(0xFFFAF6EE) else Color(0xFF0E3F2E)

val MindGoldAccent: Color
    get() = if (isAppLightTheme) Color(0xFFA67714) else Color(0xFFE5B94C)

val MindTextPrimary: Color
    get() = if (isAppLightTheme) Color(0xFF133A2B) else Color(0xFFF3F7F5)

val MindTextSecondary: Color
    get() = if (isAppLightTheme) Color(0xFF4F6B5D) else Color(0xFFA7C4B9)

val MindMintIcon: Color
    get() = if (isAppLightTheme) Color(0xFF155D43) else Color(0xFF28A77A)

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

private val MindDarkColorScheme = darkColorScheme(
    primary = Color(0xFF28A77A),
    secondary = Color(0xFFE5B94C),
    background = Color(0xFF091C16),
    surface = Color(0xFF0E3F2E),
    onPrimary = Color(0xFFF3F7F5),
    onSecondary = Color(0xFF091C16),
    onBackground = Color(0xFFF3F7F5),
    onSurface = Color(0xFFF3F7F5)
)

private val MindLightColorScheme = lightColorScheme(
    primary = Color(0xFF155D43),
    secondary = Color(0xFFA67714),
    background = Color(0xFFF6EEDB),
    surface = Color(0xFFFAF6EE),
    onPrimary = Color(0xFFF6EEDB),
    onSecondary = Color(0xFFF6EEDB),
    onBackground = Color(0xFF133A2B),
    onSurface = Color(0xFF133A2B)
)

@Composable
fun MindTheme(
    lightTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    isAppLightTheme = lightTheme
    val scheme = if (lightTheme) MindLightColorScheme else MindDarkColorScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = MindTypography,
        content = content
    )
}
