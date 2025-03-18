package dev.charan.batteryTracker.widgets

import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo

data class WidgetState(
    val deviceBattery : BatteryInfo,
    val bluetoothBattery : BluetoothDeviceBatteryInfo
)