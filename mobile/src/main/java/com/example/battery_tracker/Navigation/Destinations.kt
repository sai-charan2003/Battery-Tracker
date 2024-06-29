package com.example.battery_tracker.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination (val route:String, val imageVector: ImageVector, val label:String){
    object Home:Destination("Battery", label = "Battery", imageVector = Icons.Filled.BatteryStd)

    object settings:Destination("Settings",label="Settings", imageVector = Icons.Filled.Settings)
}