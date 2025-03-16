package dev.charan.batteryTracker.widgets

import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo

data class WidgetState(
    val deviceName : String ="",
    val batteryInfo: BatteryInfo = BatteryInfo(),
    val bluetoothBatteryInfo : BluetoothDeviceBatteryInfo = BluetoothDeviceBatteryInfo(),
)