package dev.charan.batteryTracker.presentation.home.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.charan.batteryTracker.data.model.BatteryInfo

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BatteryLevelDisplay(batteryState: BatteryInfo) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Bottom) {
        Text(
            text = batteryState.batteryLevel,
            style = MaterialTheme.typography.displayLargeEmphasized

        )
        Text(
            text = "%",
            style = MaterialTheme.typography.headlineLargeEmphasized,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
    BatteryProgressIndicator(
        percentage = batteryState.batteryPercentage,
        isLowPowerMode = batteryState.isLowPowerMode
    )
}