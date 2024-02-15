package com.example.battery_tracker.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination (val Route:String,val imageVector: ImageVector,val label:String){
    object Home:Destination("Battery", label = "Battery", imageVector = Icons.Filled.BatteryStd)
    object deviceinfo:Destination("Device Info",label="Device Info", imageVector = Icons.Filled.Info)
    object settings:Destination("Settings",label="Settings", imageVector = Icons.Filled.Settings)
}