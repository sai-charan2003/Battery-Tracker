package dev.charan.batteryTracker.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BatteryProgressIndicator(
    modifier: Modifier = Modifier,
    percentage: Float,
    isLowPowerMode: Boolean
) {
    val animatedProgress = animateFloatAsState(targetValue = percentage, label = "progress")
    LinearProgressIndicator(
        progress = { animatedProgress.value },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                10.dp)
            .size(10.dp),
        color = if(isLowPowerMode) Color.Yellow else Color.Green,
    )
}