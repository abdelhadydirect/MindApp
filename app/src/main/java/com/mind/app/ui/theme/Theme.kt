package com.mind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mind.app.R

val MindBgDark = Color(0xFF091C16)          // الخلفية الزمردية العميقة
val MindCardTop = Color(0xFF19664B)         // أعلى كارت الصلاة
val MindCardBottom = Color(0xFF13523C)      // أسفل كارت الصلاة
val MindInnerSurface = Color(0xFF0E3F2E)    // الكروت الداخلية وشريط التبويبات
val MindGoldAccent = Color(0xFFE5B94C)      // الذهبي المميز للصلاة القادمة وشريط التقدم
val MindTextPrimary = Color(0xFFF3F7F5)     // أبيض دافئ مريح للعين
val MindTextSecondary = Color(0xFFA7C4B9)   // أخضر رمادي هادئ
val MindMintIcon = Color(0xFF28A77A)        // لون أيقونة العقل

// Unified App Font (Cairo - matches the Hijri date font in the original design)
val CairoFontFamily = FontFamily(
    Font(R.font.cairo, FontWeight.Normal),
    Font(R.font.cairo, FontWeight.Medium),
    Font(R.font.cairo, FontWeight.SemiBold),
    Font(R.font.cairo, FontWeight.Bold)
)

// Quranic Script Font (Amiri Quran)
val QuranFontFamily = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal)
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
    primary = MindMintIcon,
    secondary = MindGoldAccent,
    background = MindBgDark,
    surface = MindInnerSurface,
    onPrimary = MindTextPrimary,
    onSecondary = MindBgDark,
    onBackground = MindTextPrimary,
    onSurface = MindTextPrimary
)

@Composable
fun MindTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindDarkColorScheme,
        typography = MindTypography,
        content = content
    )
}
