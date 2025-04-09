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
import dev.charan.batteryTracker.utils.NotificationHelper
import dev.charan.batteryTracker.utils.convertToBatteryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class BatteryInfoRepoImp @Inject constructor(
    @ApplicationContext val context : Context,
    val sharedPref: SharedPref,
    val notificationHelper: NotificationHelper
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun getBluetoothBattery(): BluetoothDeviceBatteryInfo {
        val headPhoneBatteryLevel = getHeadPhoneBatteryInfo()
        val wearOsBattery = registerWearOsBatteryReceiver()
        Log.d("TAG", "getBluetoothBattery: $wearOsBattery")
        return BluetoothDeviceBatteryInfo(
                headPhoneBatteryPercentage = headPhoneBatteryLevel.headPhoneBatteryPercentage,
                headPhoneName = headPhoneBatteryLevel.headPhoneName,
                headPhoneBatteryLevel = headPhoneBatteryLevel.headPhoneBatteryLevel,
                isHeadPhoneConnected = headPhoneBatteryLevel.isHeadPhoneConnected,
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun getBatteryDetails(): StateFlow<BatteryInfo?> = batteryInfoFlow.asStateFlow()

    override fun getBluetoothBatteryDetails(): Flow<BluetoothDeviceBatteryInfo?> = bluetoothBatteryInfo.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.R)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun registerWearOsBatteryReceiver() {
        var wearOSBatteryData = BatteryInfo()
        var isWearOsConnected = false
        var wearOsName : String? = null
        Wearable.getMessageClient(context).addListener {
            wearOSBatteryData = String(it.data).convertToBatteryModel()
            if(wearOSBatteryData.batteryLevel.isNullOrEmpty().not()){
                isWearOsConnected = true
                val pariedDevices : List<BluetoothDevice> = bluetoothAdapter.bondedDevices.filter { it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.WEARABLE }
                pariedDevices.forEach {
                    if(it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.WEARABLE){
                        wearOsName = it.alias

                    }
                }
            }
            if (wearOSBatteryData.batteryLevel.toDouble() <= sharedPref.minWearosBattery!!.toString().toDouble() && !sharedPref.isNotificationSent)
            {
                notificationHelper.showLowBatteryNotificationForWearos(
                    batteryLevel = wearOSBatteryData.batteryLevel,
                    deviceName = wearOSBatteryData.deviceName
                )
                sharedPref.isNotificationSent = true

            } else {
                sharedPref.isNotificationSent = false
            }

            bluetoothBatteryInfo.update {
                it.copy(
                    wearOsDeviceName = wearOsName ?: wearOSBatteryData.deviceName,
                    wearosBatteryLevel = wearOSBatteryData.batteryLevel,
                    isWearOsConnected = isWearOsConnected,
                    isWearOsCharging = wearOSBatteryData.isCharging,
                    wearOsBatteryPercentage = wearOSBatteryData.batteryPercentage
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
    override fun getHeadPhoneBatteryInfo(): BluetoothDeviceBatteryInfo {
        var headPhoneName = ""
        var headPhoneBatteryLevel = 0
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
        }
        if(headPhoneBatteryLevel.toLong() <= sharedPref.minHeadphonesBattery!!.toLong() && !sharedPref.isNotificationSentForHeadPhones){
            notificationHelper.showLowBatteryNotificationForHeadPhones(
                batteryLevel = headPhoneBatteryLevel.toString(),
                deviceName = headPhoneName
            )
            sharedPref.isNotificationSentForHeadPhones = true

        } else {
            sharedPref.isNotificationSentForHeadPhones = false
        }

        bluetoothBatteryInfo.update {
            it.copy(
                headPhoneName = headPhoneName,
                headPhoneBatteryLevel = headPhoneBatteryLevel.toString(),
                headPhoneBatteryPercentage = headPhoneBatteryLevel / 100f,
                isHeadPhoneConnected = hasHeadPhones,
            )
        }
        return BluetoothDeviceBatteryInfo(
            headPhoneName = headPhoneName,
            headPhoneBatteryLevel = headPhoneBatteryLevel.toString(),
            headPhoneBatteryPercentage = headPhoneBatteryLevel / 100f,
            isHeadPhoneConnected = hasHeadPhones,
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun sendSignalToWearOs() {
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
        return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.id }
    }
}