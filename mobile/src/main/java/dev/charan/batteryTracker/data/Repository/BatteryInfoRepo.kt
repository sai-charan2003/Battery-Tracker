package dev.charan.batteryTracker.data.Repository

import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import kotlinx.coroutines.flow.Flow

interface BatteryInfoRepo {

    fun registerBatteryReceiver()
    fun unRegisterBatteryReceiver()
    fun getBatteryDetails() : Flow<BatteryInfo?>
    fun getBluetoothBatteryDetails() : Flow<BluetoothDeviceBatteryInfo?>
    fun registerWearOsBatteryReceiver()
    fun registerBluetoothBatteryReceiver()
}