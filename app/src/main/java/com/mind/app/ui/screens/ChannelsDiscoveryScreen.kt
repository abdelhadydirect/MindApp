package com.mind.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.app.ui.theme.CairoFontFamily
import com.mind.app.ui.theme.MindBgDark
import com.mind.app.ui.theme.MindCardBottom
import com.mind.app.ui.theme.MindCardTop
import com.mind.app.ui.theme.MindGoldAccent
import com.mind.app.ui.theme.MindInnerSurface
import com.mind.app.ui.theme.MindMintIcon
import com.mind.app.ui.theme.MindTextPrimary
import com.mind.app.ui.theme.MindTextSecondary

private enum class ChannelPlatform(val label: String) {
    ALL("الكل"),
    TELEGRAM("تليجرام"),
    YOUTUBE("يوتيوب")
}

private data class DirectoryChannel(
    val id: String,
    val name: String,
    val handle: String,
    val platform: ChannelPlatform,
    val category: String,
    val country: String,
    val subscribersCount: Long,
    val description: String,
    val url: String
)

private fun formatSubscriberCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.0fK", count / 1_000.0)
        else -> count.toString()
    }
}

private val CURATED_TOP_CHANNELS = listOf(
    // YouTube Top Channels
    DirectoryChannel("yt1", "الدحيح - New Media Academy", "@Da7ee7", ChannelPlatform.YOUTUBE, "بودكاست وثقافة", "مصر", 7_400_000, "تبسيط العلوم والتاريخ والفلسفة بأسلوب شيق وممتع", "https://www.youtube.com/@Da7ee7"),
    DirectoryChannel("yt2", "دروس أونلاين - أحمد أبو زيد", "@DroosOnline4u", ChannelPlatform.YOUTUBE, "تطوير ذات وكتب", "مصر", 6_900_000, "تطوير الذات، الإنتاجية، تعلم اللغات، وإدارة الوقت", "https://www.youtube.com/@DroosOnline4u"),
    DirectoryChannel("yt3", "إذاعة ثمانية (فنجان)", "@thmanyahPodcasts", ChannelPlatform.YOUTUBE, "بودكاست وثقافة", "السعودية", 4_800_000, "أشهر بودكاست عربي في الفكر والأعمال وتجارب الحياة", "https://www.youtube.com/@thmanyahPodcasts"),
    DirectoryChannel("yt4", "الشيخ مشاري راشد العفاسي", "@Alafasy", ChannelPlatform.YOUTUBE, "ديني وقرآن", "الخليج", 9_200_000, "تلاوات قرآنية خاشعة وأناشيد إسلامية", "https://www.youtube.com/@Alafasy"),
    DirectoryChannel("yt5", "الزيرو ويب سكول - أسامة الزيرو", "@ElzeroWebSchool", ChannelPlatform.YOUTUBE, "تقنية و AI", "مصر", 1_650_000, "تعليم البرمجة وتطوير الويب من الصفر للاحتراف", "https://www.youtube.com/@ElzeroWebSchool"),
    DirectoryChannel("yt6", "د. كريم علي - فكر تاني", "@FekrTany", ChannelPlatform.YOUTUBE, "رياضة وتغذية", "مصر", 6_300_000, "التغذية العلاجية، الفسيولوجي، وبناء الصحة", "https://www.youtube.com/@FekrTany"),
    DirectoryChannel("yt7", "علي محمد علي - ملخصات كتب", "@AliMuhammadAli", ChannelPlatform.YOUTUBE, "تطوير ذات وكتب", "مصر", 3_100_000, "ملخصات أعمق الكتب في علم النفس والعادات والنجاح", "https://www.youtube.com/@AliMuhammadAli"),
    DirectoryChannel("yt8", "كابتن جيم - تغذية وتمرين", "@InformaFit", ChannelPlatform.YOUTUBE, "رياضة وتغذية", "مصر", 2_400_000, "أنظمة التغذية الاقتصادية، التضخيم والتنشيف العلمي", "https://www.youtube.com/@InformaFit"),
    DirectoryChannel("yt9", "مخيمر - الاقتصاد والبيزنس", "@Mekhemer", ChannelPlatform.YOUTUBE, "اقتصاد وأعمال", "مصر", 1_950_000, "تحليل الأسواق، ريادة الأعمال، وبناء المشاريع", "https://www.youtube.com/@Mekhemer"),
    DirectoryChannel("yt10", "Fireship - Tech & AI", "@Fireship", ChannelPlatform.YOUTUBE, "تقنية و AI", "عالمي", 3_500_000, "أحدث أخبار الذكاء الاصطناعي والبرمجة في 100 ثانية", "https://www.youtube.com/@Fireship"),

    // Telegram Top Channels
    DirectoryChannel("tg1", "تلاوات خاشعة ومختارات قرآنية", "@TilawatQuran", ChannelPlatform.TELEGRAM, "ديني وقرآن", "السعودية", 2_150_000, "مقاطع قرآنية يومية قصيرة بجودة صوتية نقية", "https://t.me/s/TilawatQuran"),
    DirectoryChannel("tg2", "مكتبة الكتب PDF العالمية", "@ArabicBooksPDF", ChannelPlatform.TELEGRAM, "تطوير ذات وكتب", "مصر", 1_850_000, "آلاف الكتب والروايات والمراجع في شتى المجالات", "https://t.me/s/ArabicBooksPDF"),
    DirectoryChannel("tg3", "أخبار الذكاء الاصطناعي والتقنية", "@AITechArabic", ChannelPlatform.TELEGRAM, "تقنية و AI", "عالمي", 1_120_000, "أحدث أدوات الذكاء الاصطناعي والتطبيقات والمواقع", "https://t.me/s/AITechArabic"),
    DirectoryChannel("tg4", "خواطر واقتباسات ملهمة", "@MindQuotesAr", ChannelPlatform.TELEGRAM, "تطوير ذات وكتب", "مصر", 940_000, "جرعات يومية من الهدوء والصفاء الذهني والكتب", "https://t.me/s/MindQuotesAr"),
    DirectoryChannel("tg5", "موسوعة التغذية وكمال الأجسام", "@FitnessNutritionEG", ChannelPlatform.TELEGRAM, "رياضة وتغذية", "مصر", 680_000, "جداول التغذية، حساب السعرات، وشرح المكملات", "https://t.me/s/FitnessNutritionEG"),
    DirectoryChannel("tg6", "فرص العمل والعمل الحر", "@FreelanceJobsAr", ChannelPlatform.TELEGRAM, "اقتصاد وأعمال", "الخليج", 820_000, "وظائف عن بُعد وفرص العمل الحر في الوطن العربي", "https://t.me/s/FreelanceJobsAr"),
    DirectoryChannel("tg7", "بودكاست وعي وثقافة", "@PodcastCulture", ChannelPlatform.TELEGRAM, "بودكاست وثقافة", "السعودية", 590_000, "ملخصات صوتية لأفضل حلقات البودكاست العربية", "https://t.me/s/PodcastCulture")
)

private val CATEGORIES = listOf("الكل", "تقنية و AI", "ديني وقرآن", "بودكاست وثقافة", "تطوير ذات وكتب", "رياضة وتغذية", "اقتصاد وأعمال")
private val COUNTRIES = listOf("الكل", "مصر", "السعودية", "الخليج", "عالمي")

@Composable
fun ChannelsDiscoveryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val channels = remember { mutableStateListOf<DirectoryChannel>().apply { addAll(CURATED_TOP_CHANNELS) } }
    var selectedPlatform by remember { mutableStateOf(ChannelPlatform.ALL) }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var selectedCountry by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Filter + Strict Descending Sort by Subscribers Count (Top -> Lowest)
    val rankedChannels = remember(channels.size, selectedPlatform, selectedCategory, selectedCountry, searchQuery) {
        val q = searchQuery.trim().lowercase()
        channels.filter { ch ->
            val matchPlatform = selectedPlatform == ChannelPlatform.ALL || ch.platform == selectedPlatform
            val matchCat = selectedCategory == "الكل" || ch.category == selectedCategory
            val matchCountry = selectedCountry == "الكل" || ch.country == selectedCountry
            val matchQuery = q.isBlank() ||
                ch.name.lowercase().contains(q) ||
                ch.handle.lowercase().contains(q) ||
                ch.category.lowercase().contains(q) ||
                ch.description.lowercase().contains(q)
            matchPlatform && matchCat && matchCountry && matchQuery
        }.sortedByDescending { it.subscribersCount }
    }

    fun openExternalUrl(url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MindBgDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add Channel",
                            tint = MindGoldAccent
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SubScreenTopBar(title = "دليل القنوات (Top)", onBack = onBack)
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "ابحث بالاسم أو المجال...",
                            color = MindTextSecondary,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = null, tint = MindGoldAccent)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Outlined.Close, contentDescription = null, tint = MindTextSecondary)
                            }
                        }
                    },
                    textStyle = TextStyle(
                        fontFamily = CairoFontFamily,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Right,
                        color = MindTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MindGoldAccent,
                        unfocusedBorderColor = MindTextSecondary.copy(alpha = 0.25f),
                        focusedContainerColor = MindInnerSurface,
                        unfocusedContainerColor = MindInnerSurface
                    )
                )

                // Platform Switcher (All / Telegram / YouTube)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MindInnerSurface)
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChannelPlatform.entries.forEach { platform ->
                        val isSelected = selectedPlatform == platform
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MindGoldAccent else Color.Transparent)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedPlatform = platform
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = platform.label,
                                color = if (isSelected) MindInnerSurface else MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Category Filter Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(CATEGORIES) { cat ->
                        val selected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selected) MindMintIcon else MindInnerSurface)
                                .border(
                                    1.dp,
                                    if (selected) MindMintIcon else MindGoldAccent.copy(alpha = 0.22f),
                                    CircleShape
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (selected) MindInnerSurface else MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Country Filter Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(COUNTRIES) { country ->
                        val selected = selectedCountry == country
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selected) MindCardTop else MindInnerSurface.copy(alpha = 0.8f))
                                .border(
                                    1.dp,
                                    if (selected) MindGoldAccent else MindTextSecondary.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedCountry = country
                                }
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Public,
                                contentDescription = null,
                                tint = if (selected) MindGoldAccent else MindTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = country,
                                color = MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
        ) {
            // Live External Telemetr & YouTube Search Banner when typing a query
            if (searchQuery.isNotBlank()) {
                item {
                    val encoded = Uri.encode(searchQuery.trim())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                brush = Brush.horizontalGradient(listOf(MindCardTop, MindCardBottom))
                            )
                            .border(1.dp, MindGoldAccent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MindInnerSurface)
                                .clickable {
                                    openExternalUrl("https://telemetr.io/en/channels?query=$encoded")
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بحث حي في Telemetr",
                                color = Color(0xFF0088CC),
                                fontFamily = CairoFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MindInnerSurface)
                                .clickable {
                                    openExternalUrl("https://www.youtube.com/results?search_query=$encoded&sp=EgIQAg%253D%253D")
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "قنوات يوتيوب الحية",
                                color = Color(0xFFD32F2F),
                                fontFamily = CairoFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            itemsIndexed(rankedChannels, key = { _, item -> item.id }) { index, channel ->
                val rank = index + 1
                val isTelegram = channel.platform == ChannelPlatform.TELEGRAM
                val platformColor = if (isTelegram) Color(0xFF0088CC) else Color(0xFFD32F2F)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MindInnerSurface)
                        .border(
                            width = if (rank <= 3) 1.5.dp else 1.dp,
                            color = if (rank <= 3) MindGoldAccent.copy(alpha = 0.55f) else MindGoldAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            openExternalUrl(channel.url)
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Rank Badge (#1, #2, #3...)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (rank <= 3) MindGoldAccent.copy(alpha = 0.22f)
                                        else MindBgDark.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        1.dp,
                                        if (rank <= 3) MindGoldAccent else MindTextSecondary.copy(alpha = 0.25f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rank == 1) {
                                    Icon(
                                        imageVector = Icons.Outlined.EmojiEvents,
                                        contentDescription = null,
                                        tint = MindGoldAccent,
                                        modifier = Modifier.size(19.dp)
                                    )
                                } else {
                                    Text(
                                        text = "#$rank",
                                        color = if (rank <= 3) MindGoldAccent else MindTextSecondary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = channel.name,
                                    color = MindTextPrimary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isTelegram) Icons.AutoMirrored.Outlined.Send else Icons.Outlined.PlayCircleOutline,
                                        contentDescription = null,
                                        tint = platformColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${channel.platform.label} • ${channel.country} • ${channel.category}",
                                        color = MindTextSecondary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Subscribers Count Pill
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MindCardTop.copy(alpha = 0.65f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.People,
                                    contentDescription = null,
                                    tint = MindMintIcon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatSubscriberCount(channel.subscribersCount),
                                    color = MindTextPrimary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = channel.description,
                            color = MindTextSecondary,
                            fontFamily = CairoFontFamily,
                            fontSize = 12.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = null,
                            tint = MindGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newUrl by remember { mutableStateOf("") }
        var newSubs by remember { mutableStateOf("500000") }
        var newCat by remember { mutableStateOf("تقنية و AI") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = MindInnerSurface,
            title = {
                Text(
                    text = "إضافة قناة للدليل",
                    color = MindGoldAccent,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = { Text("اسم القناة", fontFamily = CairoFontFamily) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        placeholder = { Text("رابط القناة (t.me/... أو youtube.com/...)", fontFamily = CairoFontFamily) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubs,
                        onValueChange = { newSubs = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("عدد المشتركين", fontFamily = CairoFontFamily) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newCat,
                        onValueChange = { newCat = it },
                        placeholder = { Text("التصنيف", fontFamily = CairoFontFamily) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank() && newUrl.isNotBlank()) {
                            val isTg = newUrl.contains("t.me", ignoreCase = true)
                            channels.add(
                                DirectoryChannel(
                                    id = "custom_${System.currentTimeMillis()}",
                                    name = newName.trim(),
                                    handle = "",
                                    platform = if (isTg) ChannelPlatform.TELEGRAM else ChannelPlatform.YOUTUBE,
                                    category = newCat.ifBlank { "بودكاست وثقافة" },
                                    country = "مصر",
                                    subscribersCount = newSubs.toLongOrNull() ?: 100_000L,
                                    description = "قناة مضافة يدوياً إلى دليلك الشخصي",
                                    url = if (newUrl.startsWith("http")) newUrl.trim() else "https://${newUrl.trim()}"
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("إضافة", color = MindGoldAccent, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء", color = MindTextSecondary, fontFamily = CairoFontFamily)
                }
            }
        )
    }
}
