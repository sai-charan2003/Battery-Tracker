package com.example.battery_widget.Navigation

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
import com.example.battery_widget.Screens.DevieInfoScreen
import com.example.battery_widget.Screens.Settings
import com.example.battery_widget.Screens.uiscreen


@Composable


fun NavigationApphost(navController: NavHostController){
    var visible by remember { mutableStateOf(true) }
    val context= LocalContext.current
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