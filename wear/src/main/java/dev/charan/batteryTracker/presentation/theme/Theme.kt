package dev.charan.batteryTracker.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun BatteryTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}