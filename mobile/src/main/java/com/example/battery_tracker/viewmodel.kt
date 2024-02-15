package com.example.battery_tracker
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class viewmodel(application: Context):ViewModel() {
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

    var chargecompute by
    mutableStateOf("null")
    var islowpowermode by mutableStateOf(false)


    fun batterydata() {



        val batteryStatsReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent?) {


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
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val lowfilter=IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        application.registerReceiver(batteryStatsReceiver, filter)
        application.registerReceiver(batteryStatsReceiver, lowfilter)


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





