package com.example.battery_tracker
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_tracker.widgets.material3.Material3widget
import com.example.battery_tracker.widgets.transparent.TransparentWidget
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class viewModel(application: Context):ViewModel() {
    val application=application




    var batterylevel by
    mutableStateOf(0)

    var batterystatus by
    mutableStateOf(0)

    var batterytype by
    mutableStateOf<String>("0")

    var healthinfo by
    mutableStateOf(0)

    var temp by
    mutableStateOf(0)
    var islowpower by
    mutableStateOf(false)
    var tempInCelsius by
    mutableStateOf(0f)

    var voltage by
    mutableStateOf(0)

    var remainingcapacity by
    mutableStateOf(0)

    var chargingstatus by
    mutableStateOf(0)
    var ischargingstatus by
    mutableStateOf("null")
    var healthstate by
    mutableStateOf("null")
    var chargingtype by
    mutableStateOf("null")

    var bluetoothBattery by
            mutableStateOf("null")

    var headPhoneName by
            mutableStateOf("null")
    var wearosBattery by
            mutableStateOf("null")
    var wearosName by
    mutableStateOf("null")

    var chargecompute by
    mutableStateOf("null")
    var islowpowermode by mutableStateOf(false)


    fun batteryData() {



        val batteryStatsReceiver = object : BroadcastReceiver() {

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onReceive(context: Context, intent: Intent?) {
                phoneBattery(context)
                bluetoothBattery(context)
                viewModelScope.launch {
                    Material3widget.updateAll(context)
                    TransparentWidget.updateAll(context)
                }

            }


        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val lowfilter=IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        application.registerReceiver(batteryStatsReceiver, filter)
        application.registerReceiver(batteryStatsReceiver, lowfilter)


    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun bluetoothBattery(context:Context){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (ActivityCompat.checkSelfPermission(
                context ,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
            val pariedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            pariedDevice.forEach {
                var headphonebattery= it?.let { bluetoothDevice ->
                    (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                        .invoke(it) as Int

                }?:-1
                if(headphonebattery!=-1){
                    headPhoneName= it.alias.toString()

                    bluetoothBattery=headphonebattery.toString()
                }
                val bluetoothClass=it.bluetoothClass
                if(bluetoothClass!=null && bluetoothClass.majorDeviceClass== BluetoothClass.Device.Major.WEARABLE){
                    val uuids = it.uuids
                    // Check UUIDs here if needed

                    wearosName= it.alias.toString()
                    viewModelScope.launch(Dispatchers.IO) {
                        val text = ""
                        var transcriptionNodeId: String? = null
                        transcriptionNodeId = com.example.battery_tracker.Screens.getNodes(context)
                            .forEach { nodeId ->
                            Wearable.getMessageClient(context).sendMessage(
                                nodeId,
                                "/deploy",
                                text.toByteArray()
                            ).apply {
                                addOnSuccessListener {


                                }
                                addOnFailureListener {

                                }
                            }


                        }.toString()


                    }


                }
            }
        }
    }

    fun phoneBattery(context:Context){
        val batteryManager = application.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryIntent = application.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        var transcriptionNodeId: String? = null
        val text="Get Battery"



        batterylevel =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        remainingcapacity =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        batterytype =
            batteryIntent!!.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).toString()
        healthinfo = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null) {
            islowpower=powerManager.isPowerSaveMode
        }
        temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        tempInCelsius = temp / 10.0f
        voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)



        healthstate = gethealthdata(healthinfo)

        chargingstatus = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        batterystatus = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
        ischargingstatus = getchargingstatus(batterystatus)

        chargingtype = getplugged(chargingstatus)
        chargecompute = (batteryManager.computeChargeTimeRemaining() / 60000f
                ).toString().substringBefore(".")
    }




}


fun gethealthdata(health: Int): String {
    var healthstate: String = "null"

    when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> healthstate = "Good"
        BatteryManager.BATTERY_HEALTH_UNKNOWN -> healthstate = "Unknown"
        BatteryManager.BATTERY_HEALTH_COLD -> healthstate = "Cold"
        BatteryManager.BATTERY_HEALTH_DEAD -> healthstate = "Dead"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> healthstate = "Over Heat"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> healthstate = "Over Voltage"


    }
    return healthstate

}

fun getchargingstatus(status: Int): String {
    var chargingstatus by mutableStateOf("null")
    when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> chargingstatus = "Charging"
        BatteryManager.BATTERY_STATUS_FULL -> chargingstatus = "Full"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> chargingstatus = "Discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> chargingstatus = "Not Charging"
    }
    return chargingstatus
}

fun getplugged(plugged: Int): String {
    var typeplug: String = "null"
    when (plugged) {
        BatteryManager.BATTERY_PLUGGED_AC -> typeplug = "AC"
        BatteryManager.BATTERY_PLUGGED_USB -> typeplug = "USB"
        BatteryManager.BATTERY_PLUGGED_DOCK -> typeplug = "Dock"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> typeplug = "Wireless"

    }
    return typeplug
}







