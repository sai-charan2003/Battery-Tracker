package dev.charan.batteryTracker.data.repository.impl

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.repository.BatteryInfoRepo
import dev.charan.batteryTracker.utils.getChargingStatus

class BatteryInfoRepoImp(
    @ApplicationContext val context : Context
) : BatteryInfoRepo {
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    override fun getBatteryDetails(): BatteryInfo {
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val batteryStatus = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, 0).getChargingStatus()
        return BatteryInfo(
            batteryLevel = batteryLevel.toString(),
            batteryPercentage = batteryLevel / 100f,
            isCharging = batteryStatus == "Charging",
            deviceName = Build.BRAND + "Wear Os"
        )




    }

}