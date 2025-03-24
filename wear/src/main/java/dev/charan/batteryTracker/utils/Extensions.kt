package dev.charan.batteryTracker.utils

import android.os.BatteryManager
import dev.charan.batteryTracker.data.model.BatteryInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun String.convertToBatteryModel() : BatteryInfo {
    try {


        val batteryInfo: BatteryInfo = Json.decodeFromString(this)
        return batteryInfo
    } catch (e:Exception){
        return BatteryInfo()
    }
}

fun Int?.getChargingStatus() : String{
    when (this) {
        BatteryManager.BATTERY_STATUS_CHARGING -> return  "Charging"
        BatteryManager.BATTERY_STATUS_FULL ->return "Full"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> return "Discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> return "Not Charging"
    }
    return "chargingstatus"
}

fun BatteryInfo.convertToJsonString() : String{
    return Json.encodeToString(this)

}