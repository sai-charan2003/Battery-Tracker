package dev.charan.batteryTracker.utils

import android.os.BatteryManager

object BatteryUtils {
    
    fun Int?.getHealthData() : String {
        when(this){
            BatteryManager.BATTERY_HEALTH_GOOD -> return  "Good"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> return "Unknown"
            BatteryManager.BATTERY_HEALTH_COLD -> return "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> return "Dead"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> return "Over Heat"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> return "Over Voltage"
        }
        return ""
    }
    
    fun Int?.getChargingStatus() : String{
        when (this) {
            BatteryManager.BATTERY_STATUS_CHARGING -> return  "Charging"
            BatteryManager.BATTERY_STATUS_FULL ->return "Full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> return "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> return "Not Charging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> return "Unknown"
        }
        return "Unknown"
    }
    
    fun Int?.getPluggedType() : String{
        when (this) {
            BatteryManager.BATTERY_PLUGGED_AC -> return "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> return "USB"
            BatteryManager.BATTERY_PLUGGED_DOCK -> return "Dock"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> return "Wireless"

        }
        return ""
        
    }
}