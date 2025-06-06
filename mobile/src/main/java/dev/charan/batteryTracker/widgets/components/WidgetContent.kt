package dev.charan.batteryTracker.widgets.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import dev.charan.batteryTracker.R
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo

@GlanceComposable
@Composable
fun WidgetContent(
    phoneBatteryState : BatteryInfo,
    bluetoothBatteryState : BluetoothDeviceBatteryInfo,
    modifier : GlanceModifier = GlanceModifier,
    isLargeWidget : Boolean
) {

        Column(
            modifier = GlanceModifier
                    .cornerRadius(15.dp)
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp, top = 15.dp)
                .then(modifier)
            ,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            DeviceBatteryView(
                deviceName = phoneBatteryState.deviceName,
                deviceBattery = phoneBatteryState.batteryLevel,
                batteryPercentage = phoneBatteryState.batteryPercentage,
                isCharging = phoneBatteryState.isCharging,
                isLowPowerMode = phoneBatteryState.isLowPowerMode,
                isLargeWidget = isLargeWidget,
                deviceIcon = ImageProvider(R.drawable.mobile),
                modifier = GlanceModifier.padding(bottom = 20.dp)
            )

            if (bluetoothBatteryState.isWearOsConnected) {
                DeviceBatteryView(
                    deviceName = if(bluetoothBatteryState.wearOsDeviceName.isNullOrEmpty().not()) bluetoothBatteryState.wearOsDeviceName else "Wear os",
                    deviceBattery = bluetoothBatteryState.wearosBatteryLevel,
                    isCharging = bluetoothBatteryState.isWearOsCharging,
                    batteryPercentage = bluetoothBatteryState.wearOsBatteryPercentage,
                    isLowPowerMode = false,
                    isLargeWidget = isLargeWidget,
                    deviceIcon = ImageProvider(R.drawable.watch),
                    modifier = GlanceModifier.padding(bottom = 20.dp)
                )

            }
            if (bluetoothBatteryState.isHeadPhoneConnected) {
                DeviceBatteryView(
                    deviceName = bluetoothBatteryState.headPhoneName,
                    deviceBattery = bluetoothBatteryState.headPhoneBatteryLevel,
                    batteryPercentage = bluetoothBatteryState.headPhoneBatteryPercentage,
                    isCharging = false,
                    isLargeWidget = isLargeWidget,
                    isLowPowerMode = false,
                    deviceIcon = ImageProvider(R.drawable.headphones),
                    modifier = GlanceModifier
                )
            }

        }
    }

