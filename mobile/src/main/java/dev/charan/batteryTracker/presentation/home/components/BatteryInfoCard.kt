package dev.charan.batteryTracker.presentation.home.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.charan.batteryTracker.data.model.BatteryInfo

@Composable
fun BatteryInfoCard(batteryState : BatteryInfo) {
    ElevatedCard(

        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(Modifier.padding(top = 20.dp, start = 10.dp)) {
            DetailsInfoRow(
                title = "Remaining Capacity",
                body = "${batteryState.remainingCapacity}mAh",

                )
            DetailsInfoRow(
                title = "Battery Status",
                body = batteryState.batteryStatus,

                )
            DetailsInfoRow(
                title = "Battery Type",
                body = batteryState.batteryType,

                )
            DetailsInfoRow(
                title = "Health Info",
                body = batteryState.batteryHealth,
                modifier = Modifier
            )
            DetailsInfoRow(
                title = "Temperature",
                body = "${batteryState.batteryTemperature}°C",
                modifier = Modifier
            )
            DetailsInfoRow(
                title = "Voltage",
                body = "${batteryState.voltage}V",
                modifier = Modifier
            )

            if (batteryState.isCharging) {
                if (batteryState.chargingType != "USB") {
                    DetailsInfoRow(
                        title = "Charge Time Remaining",
                        body = "${batteryState.chargingRemainingTime} Minutes",
                        modifier = Modifier
                    )
                }

                DetailsInfoRow(
                    title = "Charging Type",
                    body = batteryState.chargingType,
                    modifier = Modifier
                )

            }
        }
    }
}