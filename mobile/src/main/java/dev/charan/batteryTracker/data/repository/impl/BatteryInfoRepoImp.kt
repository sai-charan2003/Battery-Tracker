package dev.charan.batteryTracker.data.repository.impl

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.charan.batteryTracker.utils.AppConstants
import dev.charan.batteryTracker.utils.BatteryUtils.getChargingStatus
import dev.charan.batteryTracker.utils.BatteryUtils.getHealthData
import dev.charan.batteryTracker.utils.BatteryUtils.getPluggedType
import dev.charan.batteryTracker.data.repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.data.prefs.SharedPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class BatteryInfoRepoImp @Inject constructor(
    @ApplicationContext val context : Context,
    val sharedPref: SharedPref
): BatteryInfoRepo {
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    private var batteryReceiver: BroadcastReceiver? = null
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


    private val batteryInfoFlow = MutableStateFlow<BatteryInfo?>(null)
    private val bluetoothBatteryInfo = MutableStateFlow<BluetoothDeviceBatteryInfo>(BluetoothDeviceBatteryInfo())

    override fun registerBatteryReceiver() {
        batteryReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onReceive(context: Context, intent: Intent?) {
                Log.d("TAG", "onReceive: battery status changed")
                getPhoneBatteryData()
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
    override fun getPhoneBatteryData(): BatteryInfo {
        Log.d("TAG", "updateBatteryInfo: from update battery")

        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val batteryStatus = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, 0).getChargingStatus()

        batteryInfoFlow.value = BatteryInfo(
            deviceName = sharedPref.deviceName.toString(),
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
        return batteryInfoFlow.value ?: BatteryInfo()
    }

    override fun getBluetoothBattery(): BluetoothDeviceBatteryInfo {
        return BluetoothDeviceBatteryInfo()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun getBatteryDetails(): StateFlow<BatteryInfo?> = batteryInfoFlow.asStateFlow()

    override fun getBluetoothBatteryDetails(): Flow<BluetoothDeviceBatteryInfo?> = bluetoothBatteryInfo.asStateFlow()

    override fun registerWearOsBatteryReceiver() {
        var wearOsString = ""
        var isWearOsConnected = false
        Wearable.getMessageClient(context).addListener {
            wearOsString = String(it.data)
        }
        val wearosBattery = wearOsString.substringBefore(AppConstants.WEAROS_CHARGING_DIVIDER)
        val isWearosCharging = wearOsString.substringAfter(AppConstants.WEAROS_CHARGING_DIVIDER).toBoolean()
        if(wearosBattery.isNullOrEmpty().not()){
            isWearOsConnected = true
            bluetoothBatteryInfo.update {
                it.copy(
                    wearosBatteryLevel = wearosBattery,
                    isWearOsCharging = isWearosCharging,
                    isWearOsConnected = isWearOsConnected
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun registerBluetoothBatteryReceiver() {
        var headPhoneName = ""
        var headPhoneBatteryLevel = 0
        var wearOsName = ""
        var hasHeadPhones : Boolean = false
        val pariedDevices : List<BluetoothDevice> = bluetoothAdapter.bondedDevices.filter { it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO }
        pariedDevices.forEach {
            val headPhoneBattery = it?.let { bluetoothDevice ->
                (bluetoothDevice?.javaClass?.getMethod("getBatteryLevel"))
                    ?.invoke(it) as Int
            } ?: -1
            if (headPhoneBattery != -1){
                headPhoneName = it?.alias.toString()
                headPhoneBatteryLevel = headPhoneBattery
                hasHeadPhones = true
            }
            if(it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.WEARABLE){
                sendSignalToWearOs()
                wearOsName = it.alias.toString()

            }
        }


        bluetoothBatteryInfo.update {
            it.copy(
                headPhoneName = headPhoneName,
                headPhoneBatteryLevel = headPhoneBatteryLevel.toString(),
                headPhoneBatteryPercentage = headPhoneBatteryLevel / 100f,
                isHeadPhoneConnected = hasHeadPhones,
                wearOsDeviceName = wearOsName
            )

        }



    }

    @RequiresApi(Build.VERSION_CODES.R)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendSignalToWearOs() {
        getNodes(context)
            .forEach { nodeId ->
                Wearable.getMessageClient(context).sendMessage(
                    nodeId,
                    "/deploy",
                    "".toByteArray()
                ).apply {
                    addOnSuccessListener {
                        Log.d("TAG", "sendSignalToWearOs: sent")


                    }
                    addOnFailureListener {
                        Log.d("TAG", "sendSignalToWearOs: $it")

                    }
                }
            }.toString()


    }

    private fun getNodes(context: Context): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.displayName }
    }
}