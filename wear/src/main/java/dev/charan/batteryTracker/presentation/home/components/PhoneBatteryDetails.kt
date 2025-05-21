package dev.charan.batteryTracker.presentation.home.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedIconButton
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.Text
import dev.charan.batteryTracker.R

@Composable
fun PhoneBatterDetails(
    batteryLevel : String,
    batteryPercentage: Float,
    isCharging: Boolean,
    deviceName: String,
    fetchBattery: () -> Unit
) {
    val animatedProgress = animateFloatAsState(targetValue = batteryPercentage, label = "progress")
    CircularProgressIndicator(
        progress = { animatedProgress.value },
        modifier = Modifier.fillMaxSize(),
        startAngle = 290f,
        endAngle = 250f,
        strokeWidth = 5.dp,
        colors =ProgressIndicatorDefaults.colors(
            indicatorColor = if (isCharging) Color.Green else MaterialTheme.colorScheme.primary
        )
    )
    BatteryDetails(
        deviceName = deviceName,
        isCharging = isCharging,
        batteryPercentage = batteryPercentage,
        batteryLevel = batteryLevel,
        fetchBattery = {
            fetchBattery()

        }
    )


}
@Composable
fun BatteryDetails(
    batteryLevel: String,
    deviceName: String,
    isCharging: Boolean,
    batteryPercentage : Float,
    fetchBattery : () -> Unit
) {
    Column(modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text(text= deviceName, textAlign = TextAlign.Center,modifier=Modifier.padding(bottom=10.dp))
        Row(verticalAlignment = Alignment.CenterVertically,modifier=Modifier.padding(bottom=10.dp)) {
            if(isCharging){
                Icon(painter = painterResource(id = R.drawable.charging), contentDescription = null)
            }
            Text(
                text = "${batteryLevel}%",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall

            )

        }
        IconButton(
            onClick = {
                fetchBattery()
            },

            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))


        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, device = "id:wearos_small_round")
fun PhoneBatteryDetailsPreview() {
    PhoneBatterDetails(batteryPercentage = 50f, isCharging = true, deviceName = "Phone", batteryLevel = "", fetchBattery = {})

}