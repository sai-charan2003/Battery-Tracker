package dev.charan.batteryTracker.data.Repository.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import dev.charan.batteryTracker.Utils.BatteryUtils.getChargingStatus
import dev.charan.batteryTracker.Utils.BatteryUtils.getHealthData
import dev.charan.batteryTracker.Utils.BatteryUtils.getPluggedType
import dev.charan.batteryTracker.data.Repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.model.BatteryInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class BatteryInfoRepoImp(private val context: Context) : BatteryInfoRepo {
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    private var batteryReceiver: BroadcastReceiver? = null


    private val batteryInfoFlow = MutableStateFlow<BatteryInfo?>(null)

    override fun registerBatteryReceiver() {
        if (batteryReceiver != null) return

        batteryReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onReceive(context: Context, intent: Intent?) {
                Log.d("TAG", "onReceive: battery status changed")
                updateBatteryInfo()
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }

        context.registerReceiver(batteryReceiver, filter)
    }

    override fun unRegisterBatteryReceiver() {
        batteryReceiver?.let {
            context.unregisterReceiver(it)
            batteryReceiver = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateBatteryInfo() {

        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val batteryStatus = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, 0).getChargingStatus()

        batteryInfoFlow.value = BatteryInfo(
            batteryLevel = batteryLevel.toString(),
            batteryPercentage = batteryLevel / 100f,
            remainingCapacity = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) / 1000).toString(),
            batteryStatus = batteryStatus,
            batteryHealth = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0).getHealthData(),
            batteryTemperature = (batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                ?.div(10f)).toString(),
            voltage = (batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)?.div(1000f)).toString(),
            isCharging = batteryStatus == "Charging",
            chargingType = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0).getPluggedType(),
            chargingRemainingTime = batteryManager.computeChargeTimeRemaining().toString(),
            isLowPowerMode = powerManager?.isPowerSaveMode == true,
            batteryType = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).toString()
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun getBatteryDetails(): StateFlow<BatteryInfo?> = batteryInfoFlow.asStateFlow()

    override fun getBluetoothBatteryDetails(): Flow<BatteryInfo> = flow{


    }
}