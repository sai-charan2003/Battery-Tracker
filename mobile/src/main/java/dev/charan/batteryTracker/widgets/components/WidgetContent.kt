package dev.charan.batteryTracker.widgets.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo

@GlanceComposable
@Composable
fun WidgetContent(
    phoneBatteryState : BatteryInfo,
    bluetoothBatteryState : BluetoothDeviceBatteryInfo,
    modifier : GlanceModifier = GlanceModifier
) {

        Column(
            modifier = GlanceModifier
                .cornerRadius(25.dp)
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, bottom = 5.dp, top = 15.dp)
                .then(modifier)
            ,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            DeviceBatteryView(
                deviceName = phoneBatteryState.deviceName,
                deviceBattery = phoneBatteryState.batteryLevel,
                batteryPercentage = phoneBatteryState.batteryPercentage,
                isCharging = phoneBatteryState.isCharging,
                isLowPowerMode = phoneBatteryState.isLowPowerMode,
                modifier = GlanceModifier.padding(bottom = 20.dp)
            )
            Log.d("TAG", "WidgetContent: ${bluetoothBatteryState.isWearOsConnected}")

            if (bluetoothBatteryState.isWearOsConnected) {
                DeviceBatteryView(
                    deviceName = bluetoothBatteryState.wearosBatteryLevel,
                    deviceBattery = bluetoothBatteryState.wearosBatteryLevel,
                    isCharging = bluetoothBatteryState.isWearOsCharging,
                    batteryPercentage = bluetoothBatteryState.wearOsBatteryPercentage,
                    isLowPowerMode = false,
                    modifier = GlanceModifier.padding(bottom = 20.dp)
                )

            }
            if (bluetoothBatteryState.isHeadPhoneConnected) {
                DeviceBatteryView(
                    deviceName = bluetoothBatteryState.headPhoneName,
                    deviceBattery = bluetoothBatteryState.headPhoneBatteryLevel,
                    batteryPercentage = bluetoothBatteryState.headPhoneBatteryPercentage,
                    isCharging = false,
                    isLowPowerMode = false,
                    modifier = GlanceModifier
                )
            }

        }
    }

