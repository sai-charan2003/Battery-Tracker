package dev.charan.batteryTracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.charan.batteryTracker.presentation.home.HomeScreen
import dev.charan.batteryTracker.presentation.settings.LicenseScreen
import dev.charan.batteryTracker.presentation.settings.SettingsScreen


@RequiresApi(Build.VERSION_CODES.S)
@Composable


fun NavigationAppHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeScreenNav,
        enterTransition = {
            fadeIn() + slideIntoContainer(
                SlideDirection.Start,
                initialOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(
                SlideDirection.Start,
                targetOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popEnterTransition = {
            fadeIn() + slideIntoContainer(
                SlideDirection.End,
                initialOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(
                SlideDirection.End,
                targetOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
    ) {
        composable<HomeScreenNav> {
            HomeScreen(navController)
        }
        composable<SettingsScreenNav> {
            SettingsScreen(navController)
        }
        composable<LicenseScreenNav> {
            LicenseScreen(navController)
        }


    }


}