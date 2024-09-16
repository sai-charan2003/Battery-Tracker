package dev.charan.batteryTracker.Navigation

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
import dev.charan.batteryTracker.Screens.HomeScreen
import dev.charan.batteryTracker.Screens.Settings


@RequiresApi(Build.VERSION_CODES.S)
@Composable


fun NavigationApphost(navController: NavHostController){


    NavHost(
        navController = navController,
        startDestination = dev.charan.batteryTracker.Navigation.Destination.Home.route,
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
        },){
        composable(route= Destination.Home.route){
            HomeScreen(navController)
        }
        composable(route = Destination.settings.route){
            Settings(navController)
        }




    }


}