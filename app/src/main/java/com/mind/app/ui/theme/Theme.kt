package com.mind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MindBgDark = Color(0xFF091C16)          // الخلفية الزمردية العميقة
val MindCardTop = Color(0xFF19664B)         // أعلى كارت الصلاة
val MindCardBottom = Color(0xFF13523C)      // أسفل كارت الصلاة
val MindInnerSurface = Color(0xFF0E3F2E)    // الكروت الداخلية وشريط التبويبات
val MindGoldAccent = Color(0xFFE5B94C)      // الذهبي المميز للصلاة القادمة وشريط التقدم
val MindGoldDim = Color(0x33E5B94C)         // خلفية ذهبية خفيفة
val MindTextPrimary = Color(0xFFF3F7F5)     // أبيض دافئ مريح للعين
val MindTextSecondary = Color(0xFFA7C4B9)   // أخضر رمادي هادئ
val MindMintIcon = Color(0xFF28A77A)        // لون أيقونة العقل

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
        content = content
    )
}
