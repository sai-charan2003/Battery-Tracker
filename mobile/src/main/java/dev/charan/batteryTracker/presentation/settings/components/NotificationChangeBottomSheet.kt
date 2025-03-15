package dev.charan.batteryTracker.presentation.settings.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.charan.batteryTracker.presentation.settings.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsBottomSheet(
    state: SettingsState,
    onDismiss: () -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onHeadphonesSliderChange: (Float) -> Unit,
    onRequestBluetoothPermission: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Notifications", modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Switch(checked = state.isNotificationPermissionGranted, onCheckedChange = onToggleNotification)
            }
            if (state.isNearByPermissionGranted) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Wear OS Low Battery Alert", modifier = Modifier.weight(1f))
                    Text(text = state.wearOsMinimumBattery.toString())
                }
                Slider(
                    value = state.wearOsMinimumBattery,
                    onValueChange = onSliderValueChange,
                    valueRange = 0f..90f,
                    steps = 8
                )
                Row(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Headphones Low Battery Alert",
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = state.headPhonesMinimumBattery.toString())
                }
                Slider(
                    value = state.headPhonesMinimumBattery,
                    onValueChange = onHeadphonesSliderChange,
                    valueRange = 0f..90f,
                    steps = 8
                )
            } else {
                Row(modifier = Modifier
                    .clickable { onRequestBluetoothPermission() }
                    .padding(10.dp)) {
                    Text(
                        text = "Enable Bluetooth Permission", modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}