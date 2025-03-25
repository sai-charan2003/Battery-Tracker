package dev.charan.batteryTracker.utils

import dev.charan.batteryTracker.data.model.BatteryInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun BatteryInfo.convertToJsonString() : String{
    return Json.encodeToString(this)

}
fun String.convertToBatteryModel() : BatteryInfo {
    try {
        val batteryInfo: BatteryInfo = Json.decodeFromString(this)
        return batteryInfo
    } catch (e:Exception){
        return BatteryInfo()
    }
}
