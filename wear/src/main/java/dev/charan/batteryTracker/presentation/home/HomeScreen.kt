package dev.charan.batteryTracker.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.presentation.home.components.PhoneBatterDetails

@Composable
fun HomeScreen(
    batteryInfo: BatteryInfo,
    fetchBattery: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        TimeText()
        PhoneBatterDetails(
            batteryPercentage = batteryInfo.batteryPercentage,
            isCharging = batteryInfo.isCharging,
            deviceName = batteryInfo.deviceName,
            batteryLevel = batteryInfo.batteryLevel,
            fetchBattery = {
                fetchBattery()

            }
        )

    }
}