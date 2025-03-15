package dev.charan.batteryTracker.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.charan.batteryTracker.data.model.BatteryInfo

@Composable
fun BatteryLevelDisplay(batteryState: BatteryInfo) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = batteryState.batteryLevel,
            fontWeight = FontWeight.W900,
            fontSize = 50.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "%",
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 35.dp)
        )
    }
    BatteryProgressIndicator(
        percentage = batteryState.batteryPercentage,
        isLowPowerMode = batteryState.isLowPowerMode
    )
}