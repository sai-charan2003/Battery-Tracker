package dev.charan.batteryTracker.data.model

data class BatteryInfo(
    val deviceName : String = "",
    val batteryLevel : String = "",
    val batteryPercentage : Float = 0f,
    val remainingCapacity : String = "",
    val batteryStatus : String = "",
    val batteryType : String = "",
    val batteryHealth : String = "",
    val temperature : String = "",
    val voltage : String = "",
    val chargingType : String = "",
    val batteryTemperature : String = "",
    val isCharging : Boolean = false,
    val chargingRemainingTime : String = "",
    val isLowPowerMode : Boolean = false
)
