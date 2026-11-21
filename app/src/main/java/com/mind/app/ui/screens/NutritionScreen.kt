package com.mind.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private data class MealPlanItem(
    val id: Int,
    val timeWindow: String,
    val title: String,
    val subtitle: String,
    val ingredients: List<String>,
    val physiologyEffects: List<String>
)

private data class AvoidHabitItem(
    val title: String,
    val harm: String,
    val solution: String
)

private data class SupplementPlanItem(
    val name: String,
    val benefit: String,
    val dosage: String
)

private val DAILY_MEALS = listOf(
    MealPlanItem(
        id = 1,
        timeWindow = "8:00 - 10:00 ص",
        title = "وجبة الفطار",
        subtitle = "تشغيل المحرك ووقف الهدم",
        ingredients = listOf(
            "2 رغيف عيش بلدي (ردة وألياف)",
            "3 بيضات مسلوقة (أو طبق فول كبير بزيت زيتون/حار + قطعة جبنة قريش)",
            "طماطم وخيار طازج"
        ),
        physiologyEffects = listOf(
            "بعد صيام النوم يبدأ الجسم بتكسير العضلات للطاقة؛ العيش البلدي يفرز الأنسولين باعتدال فيوقف الهدم العضلي فوراً.",
            "بروتين البيض والجبنة يمد العضلات بالأحماض الأمينية لبداية اليوم."
        )
    ),
    MealPlanItem(
        id = 2,
        timeWindow = "2:00 - 4:00 عصراً",
        title = "الغداء (قبل التمرين بساعتين)",
        subtitle = "شحن بطارية الطاقة",
        ingredients = listOf(
            "طبق مكرونة كبير (أو 2 حبة بطاطس متوسطة مسلوقة أو في الفرن)",
            "علبة تونة متصفية (أو ربع فرخة / عدس بجبة)",
            "طبق سلطة خضراء متكامل"
        ),
        physiologyEffects = listOf(
            "المكرونة والبطاطس تتحول إلى جليكوجين يُخزن داخل العضلات والكبد لرفع أوزان ثقيلة دون هبوط.",
            "التونة توفر أحماضاً أمينية في الدم تحمي الألياف العضلية من التكسير أثناء المجهود."
        )
    ),
    MealPlanItem(
        id = 3,
        timeWindow = "قبل التمرين بـ 45 دقيقة",
        title = "سناك الباور",
        subtitle = "الباور والتركيز وضخ الدم",
        ingredients = listOf(
            "حبة بطاطا حلوة (مشوية أو مسلوقة)",
            "صباع موز",
            "كوب شاي (بمعلقة سكر واحدة أو بدون)"
        ),
        physiologyEffects = listOf(
            "البطاطا والموز يمنحان طاقة سريعة ومستمرة، والبوتاسيوم يمنع الشد العضلي ويزيد ضخ الدم (Pump).",
            "الكافيين ينشط الجهاز العصبي فيرفع القوة والتركيز بنسبة 10-15%."
        )
    ),
    MealPlanItem(
        id = 4,
        timeWindow = "بعد التمرين (خلال ساعة)",
        title = "وجبة الاستشفاء",
        subtitle = "بناء الألياف العضلية",
        ingredients = listOf(
            "علبة تونة (أو 4 بياض بيض + بيضة كاملة)",
            "2 حبة بطاطس مسلوقة (أو رغيف ونص عيش بلدي)",
            "خيار وطماطم"
        ),
        physiologyEffects = listOf(
            "البطاطس والعيش يرفعان الأنسولين فيفتحان خلايا العضلات المجهدة كالإسفنجة.",
            "تندفع الأحماض الأمينية لداخل العضلة لإعادة بنائها أكبر وأقوى (الاستشفاء العضلي)."
        )
    ),
    MealPlanItem(
        id = 5,
        timeWindow = "قبل النوم بساعتين",
        title = "وجبة العشاء",
        subtitle = "التغذية الليلية البطيئة",
        ingredients = listOf(
            "قطعة جبنة قريش كبيرة (150 - 200 جرام)",
            "رغيف عيش بلدي",
            "حفنة فول سوداني غير مملح (حوالي 20 جرام)"
        ),
        physiologyEffects = listOf(
            "الجبنة القريش غنية ببروتين الكازين بطيء الهضم (6-8 ساعات) ليغذي عضلاتك أثناء نشاط هرمون النمو (GH).",
            "الدهون الصحية في الفول السوداني تدعم إنتاج هرمون التستوستيرون والبناء العضلي."
        )
    )
)

private val AVOID_HABITS = listOf(
    AvoidHabitItem(
        title = "الشاي بعد الأكل مباشرة",
        harm = "مادة التانين (Tannins) ترتبط بالحديد والزنك في الطعام وتمنع امتصاصهما، مما يسبب الأنيميا والنهجان وضعف الأداء.",
        solution = "اشربه قبل التمرين بـ 45 دقيقة كمحفز طبيعي، أو بعد الوجبات بساعتين على الأقل."
    ),
    AvoidHabitItem(
        title = "السكر الأبيض الزائد",
        harm = "يرفع الأنسولين بشكل مفاجئ ثم يهبط بسرعة مسبباً الخمول والالتهابات وتخزين الدهون حول البطن.",
        solution = "الحد الأقصى ملعقتان صغيرتان يومياً، والاعتماد على البطاطا الحلوة والموز للتحلية."
    ),
    AvoidHabitItem(
        title = "الزيوت المهدرجة والقلي الغزير",
        harm = "تقلل كفاءة ضخ الدم للعضلات وتزيد الدهون الحشوية الضارة.",
        solution = "السلق، الهرس بملعقة زيت زيتون صغيرة، أو التحمير الصحي في الفرن (ويدجز)."
    ),
    AvoidHabitItem(
        title = "السهر وقلة النوم",
        harm = "العضلة تُبنى أثناء النوم؛ السهر يرفع هرمون الكورتيزول الهدّام للعضلات والمخزّن للدهون.",
        solution = "النوم العميق من 7 إلى 8 ساعات ليلاً لضمان أقصى إفراز لهرمون النمو."
    )
)

private val ECONOMIC_SUPPLEMENTS = listOf(
    SupplementPlanItem(
        name = "كرياتين مونوهيدرات (Creatine Monohydrate)",
        benefit = "أقوى مكمل مثبت علمياً؛ يزيد القوة البدنية، يسحب الماء داخل الخلية العضلية لحجم أكبر، ويسرّع الاستشفاء.",
        dosage = "5 جرام يومياً (ملعقة شاي ممسوحة) في أي وقت مع شرب كمية وفيرة من الماء."
    ),
    SupplementPlanItem(
        name = "مالتي فيتامين (Kerovit / Vitamax Plus)",
        benefit = "يمد الجسم بالزنك، الماغنسيوم، الحديد، وفيتامينات B اللازمة لتحويل الغذاء إلى طاقة وأنسجة عضلية.",
        dosage = "كبسولة واحدة يومياً بعد وجبة الفطار أو الغداء."
    ),
    SupplementPlanItem(
        name = "أوميجا 3 (Omega 3 Plus)",
        benefit = "يقلل التهابات المفاصل والعضلات الناتجة عن التمرين المكثف ويدعم صحة القلب والدورة الدموية.",
        dosage = "كبسولة واحدة يومياً بعد وجبة الغداء."
    ),
    SupplementPlanItem(
        name = "أقراص خميرة البيرة (Yeast)",
        benefit = "مصدر اقتصادي غني بفيتامينات B والأحماض الأمينية لتحسين الهضم والامتصاص وفتح الشهية.",
        dosage = "حسب إرشادات العبوة بعد الوجبات الرئيسية."
    )
)

@Composable
fun NutritionScreen(onBack: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val completedMeals = remember { mutableStateMapOf<Int, Boolean>() }
    val expandedPhysiology = remember { mutableStateMapOf<Int, Boolean>(1 to true) }

    val completedCount = completedMeals.count { it.value }
    val progressRatio by animateFloatAsState(
        targetValue = completedCount / DAILY_MEALS.size.toFloat(),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "meal_progress"
    )

    Scaffold(
        containerColor = MindBgDark,
        topBar = {
            SubScreenTopBar(title = "النظام الغذائي والفسيولوجي", onBack = onBack)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 36.dp)
        ) {
            // 85% / 10% / 5% Formula + Daily Meal Tracker Hero Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(MindCardTop, MindCardBottom)
                            )
                        )
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.4f), RoundedCornerShape(26.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "معادلة البناء والاستشفاء",
                                color = MindTextPrimary,
                                fontFamily = CairoFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "إنجاز وجبات اليوم: $completedCount من ${DAILY_MEALS.size}",
                                color = MindGoldAccent,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MindInnerSurface)
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.45f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FitnessCenter,
                                contentDescription = null,
                                tint = MindMintIcon,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(MindInnerSurface.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressRatio)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(MindGoldAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 85% Nutrition | 10% Sleep | 5% Supplements breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FormulaBadge(
                            percent = "85%",
                            label = "التغذية",
                            modifier = Modifier.weight(1f)
                        )
                        FormulaBadge(
                            percent = "10%",
                            label = "النوم (8س)",
                            modifier = Modifier.weight(1f)
                        )
                        FormulaBadge(
                            percent = "5%",
                            label = "المكملات",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Segmented Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MindInnerSurface)
                        .border(1.dp, MindGoldAccent.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NutritionTabChip(
                        title = "الجدول",
                        icon = Icons.Outlined.Restaurant,
                        selected = selectedTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = 0
                        }
                    )
                    NutritionTabChip(
                        title = "تجنبها",
                        icon = Icons.Outlined.WarningAmber,
                        selected = selectedTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = 1
                        }
                    )
                    NutritionTabChip(
                        title = "المكملات",
                        icon = Icons.Outlined.LocalPharmacy,
                        selected = selectedTab == 2,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = 2
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    items(DAILY_MEALS, key = { it.id }) { meal ->
                        val isDone = completedMeals[meal.id] == true
                        val isExpanded = expandedPhysiology[meal.id] == true

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(MindInnerSurface)
                                .border(
                                    width = if (isDone) 1.5.dp else 1.dp,
                                    color = if (isDone) MindMintIcon else MindGoldAccent.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .padding(16.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MindGoldAccent.copy(alpha = 0.14f))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                completedMeals[meal.id] = !isDone
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isDone) MindMintIcon else MindGoldAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.Schedule,
                                                contentDescription = null,
                                                tint = MindGoldAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = meal.timeWindow,
                                                color = MindGoldAccent,
                                                fontFamily = CairoFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = meal.title,
                                            color = MindTextPrimary,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = meal.subtitle,
                                            color = MindMintIcon,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Ingredients List
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MindBgDark.copy(alpha = 0.55f))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                meal.ingredients.forEach { ing ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(
                                            text = "•",
                                            color = MindGoldAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = ing,
                                            color = MindTextPrimary,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 13.5.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Collapsible Physiological Impact Drawer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MindCardTop.copy(alpha = 0.45f))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        expandedPhysiology[meal.id] = !isExpanded
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Science,
                                        contentDescription = null,
                                        tint = MindMintIcon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "التأثير الفسيولوجي على جسمك",
                                        color = MindTextPrimary,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = null,
                                    tint = MindTextPrimary
                                )
                            }

                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    meal.physiologyEffects.forEach { effect ->
                                        Text(
                                            text = "✦ $effect",
                                            color = MindTextSecondary,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    items(AVOID_HABITS) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MindInnerSurface)
                                .border(1.dp, Color(0xFFD96B43).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                    tint = Color(0xFFD96B43),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    color = MindTextPrimary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "الضرر: ${item.harm}",
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MindCardTop.copy(alpha = 0.55f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "الحل: ${item.solution}",
                                    color = MindMintIcon,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                2 -> {
                    items(ECONOMIC_SUPPLEMENTS) { supp ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MindInnerSurface)
                                .border(1.dp, MindGoldAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Bolt,
                                    contentDescription = null,
                                    tint = MindGoldAccent,
                                    modifier = Modifier.size(21.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = supp.name,
                                    color = MindTextPrimary,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = supp.benefit,
                                color = MindTextSecondary,
                                fontFamily = CairoFontFamily,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MindBgDark.copy(alpha = 0.65f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "الجرعة: ${supp.dosage}",
                                    color = MindGoldAccent,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormulaBadge(
    percent: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MindInnerSurface.copy(alpha = 0.92f))
            .border(1.dp, MindGoldAccent.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = percent,
            color = MindGoldAccent,
            fontFamily = CairoFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MindTextPrimary,
            fontFamily = CairoFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NutritionTabChip(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) MindGoldAccent else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MindInnerSurface else MindTextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = title,
            color = if (selected) MindInnerSurface else MindTextPrimary,
            fontFamily = CairoFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
