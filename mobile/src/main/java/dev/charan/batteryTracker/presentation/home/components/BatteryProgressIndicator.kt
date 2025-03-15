package dev.charan.batteryTracker.presentation.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BatteryProgressIndicator(
    modifier: Modifier = Modifier,
    percentage: Float,
    isLowPowerMode: Boolean
) {
    LinearProgressIndicator(
        progress = { percentage },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                10.dp)
            .size(10.dp),
        color = if(isLowPowerMode) Color.Yellow else Color.Green,
    )
}