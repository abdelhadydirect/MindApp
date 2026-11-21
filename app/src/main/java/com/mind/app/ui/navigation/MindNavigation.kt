package com.mind.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mind.app.ui.MindViewModel
import com.mind.app.ui.screens.HomeScreen
import com.mind.app.ui.screens.NotesScreen
import com.mind.app.ui.screens.NutritionScreen
import com.mind.app.ui.screens.QuotesEditorScreen
import com.mind.app.ui.screens.QuranScreen
import com.mind.app.ui.screens.SettingsScreen
import com.mind.app.ui.screens.TasksScreen

sealed class MindRoute(val route: String) {
    data object Home : MindRoute("home")
    data object Quran : MindRoute("quran")
    data object Notes : MindRoute("notes")
    data object Tasks : MindRoute("tasks")
    data object Nutrition : MindRoute("nutrition")
    data object Quotes : MindRoute("quotes")
    data object Settings : MindRoute("settings")
}

@Composable
fun MindNavGraph(viewModel: MindViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MindRoute.Home.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(280)) + fadeIn(tween(240))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(280)) + fadeOut(tween(240))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(280)) + fadeIn(tween(240))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(280)) + fadeOut(tween(240))
        }
    ) {
        composable(MindRoute.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToQuran = { navController.navigate(MindRoute.Quran.route) },
                onNavigateToNotes = { navController.navigate(MindRoute.Notes.route) },
                onNavigateToTasks = { navController.navigate(MindRoute.Tasks.route) },
                onNavigateToNutrition = { navController.navigate(MindRoute.Nutrition.route) },
                onNavigateToQuotesEditor = { navController.navigate(MindRoute.Quotes.route) },
                onNavigateToSettings = { navController.navigate(MindRoute.Settings.route) }
            )
        }
        composable(MindRoute.Quran.route) {
            QuranScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MindRoute.Notes.route) {
            NotesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MindRoute.Tasks.route) {
            TasksScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MindRoute.Nutrition.route) {
            NutritionScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(MindRoute.Quotes.route) {
            QuotesEditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MindRoute.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
