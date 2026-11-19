package com.mind.app.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.screens.HomeScreen
import com.mind.app.ui.screens.NotesScreen
import com.mind.app.ui.screens.QuotesEditorScreen
import com.mind.app.ui.screens.SettingsScreen
import com.mind.app.ui.screens.TasksScreen

@Composable
fun MindAppRoot(viewModel: MindViewModel = viewModel()) {
    val navController = rememberNavController()
    val springSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Enforce RTL layout across the entire app
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = {
                slideInVertically(animationSpec = springSpec) { it / 6 } +
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    scaleIn(initialScale = 0.96f)
            },
            exitTransition = {
                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                    scaleOut(targetScale = 0.97f)
            },
            popEnterTransition = {
                fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    scaleIn(initialScale = 0.97f)
            },
            popExitTransition = {
                slideOutVertically(animationSpec = springSpec) { it / 6 } +
                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            }
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToTasks = { navController.navigate("tasks") },
                    onNavigateToQuotes = { navController.navigate("quotes") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("notes") {
                NotesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("tasks") {
                TasksScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("quotes") {
                QuotesEditorScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("settings") {
                SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
