package com.dragonest.artifacts.goo.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dragonest.artifacts.goo.ui.screens.GameScreen
import com.dragonest.artifacts.goo.ui.screens.HowToPlayScreen
import com.dragonest.artifacts.goo.ui.screens.LeaderboardScreen
import com.dragonest.artifacts.goo.ui.screens.LevelsScreen
import com.dragonest.artifacts.goo.ui.screens.MenuScreen
import com.dragonest.artifacts.goo.ui.screens.PrivacyPolicyScreen
import com.dragonest.artifacts.goo.ui.screens.SettingsScreen

@Composable
fun AppNavHost(
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MENU,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(260)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(260)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) },
    ) {
        composable(NavRoutes.MENU) {
            MenuScreen(
                onPlay = { navController.navigate(NavRoutes.LEVELS) },
                onSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onLeaderboard = { navController.navigate(NavRoutes.LEADERBOARD) },
                onExit = onExitApp,
            )
        }
        composable(NavRoutes.LEVELS) {
            LevelsScreen(
                onBack = { navController.popBackStack() },
                onLevelChosen = { lvl ->
                    navController.navigate(NavRoutes.game(lvl))
                },
            )
        }
        composable(
            route = NavRoutes.GAME,
            arguments = listOf(navArgument(NavRoutes.ARG_LEVEL) { type = NavType.IntType }),
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt(NavRoutes.ARG_LEVEL) ?: 1
            GameScreen(
                level = level,
                onBackToMenu = {
                    navController.popBackStack(NavRoutes.MENU, inclusive = false)
                },
                onBackToLevels = {
                    navController.popBackStack(NavRoutes.LEVELS, inclusive = false)
                },
                onGoToLevel = { nextLevel ->
                    navController.navigate(NavRoutes.game(nextLevel)) {
                        popUpTo(NavRoutes.LEVELS) { inclusive = false }
                    }
                },
            )
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onHowToPlay = { navController.navigate(NavRoutes.HOW_TO_PLAY) },
                onPrivacy = { navController.navigate(NavRoutes.PRIVACY) },
            )
        }
        composable(NavRoutes.HOW_TO_PLAY) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.PRIVACY) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
