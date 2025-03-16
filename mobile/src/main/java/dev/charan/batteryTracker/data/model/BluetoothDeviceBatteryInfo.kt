package dev.charan.batteryTracker.data.model

data class BluetoothDeviceBatteryInfo(
    val isHeadPhoneConnected : Boolean = false,
    val headPhoneBatteryLevel : String = "",
    val headPhoneBatteryPercentage : Float = 0f,
    val headPhoneName : String = "",
    val isWearOsConnected : Boolean = false,
    val wearosBatteryLevel : String = "",
    val wearOsDeviceName : String = "",
    val wearOsBatteryPercentage : Float = 0f,
    val isWearOsCharging : Boolean = false
)
