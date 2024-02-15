package com.example.battery_tracker.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.battery_tracker.Screens.DevieInfoScreen
import com.example.battery_tracker.Screens.Settings
import com.example.battery_tracker.Screens.uiscreen


@RequiresApi(Build.VERSION_CODES.S)
@Composable


fun NavigationApphost(navController: NavHostController){
    val uri = "https://www.example.com"

    NavHost(navController = navController, startDestination = Destination.Home.Route, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None}){
        composable(route=Destination.Home.Route){

            uiscreen()
        }
        composable(route=Destination.deviceinfo.Route){
            DevieInfoScreen(navController)
        }
        composable(route=Destination.settings.Route){
            Settings(navController)
        }



    }


}