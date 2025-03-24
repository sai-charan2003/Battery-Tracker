package dev.charan.batteryTracker.presentation.home.components

import android.util.Log
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
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import dev.charan.batteryTracker.R

@Composable
fun PhoneBatterDetails(
    batteryLevel : String,
    batteryPercentage: Float,
    isCharging: Boolean,
    deviceName: String
) {
    CircularProgressIndicator(
        progress = batteryPercentage,
        modifier = Modifier.fillMaxSize(),
        startAngle = 290f,
        endAngle = 250f,
        strokeWidth = 4.dp,
        indicatorColor = if(isCharging) Color.Green else Color.Blue
    )
    BatteryDetails(
        deviceName = deviceName,
        isCharging = isCharging,
        batteryPercentage = batteryPercentage,
        batteryLevel = batteryLevel
    )


}
@Composable
fun BatteryDetails(
    batteryLevel: String,
    deviceName: String,
    isCharging: Boolean,
    batteryPercentage : Float
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
                style = MaterialTheme.typography.title1

            )

        }
        OutlinedButton(
            onClick = {
            },
            modifier = Modifier
                .size(ButtonDefaults.ExtraSmallButtonSize)
                .padding(bottom = 10.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)


        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, device = "id:wearos_small_round")
fun PhoneBatteryDetailsPreview() {
    PhoneBatterDetails(batteryPercentage = 50f, isCharging = true, deviceName = "Phone", batteryLevel = "")

}