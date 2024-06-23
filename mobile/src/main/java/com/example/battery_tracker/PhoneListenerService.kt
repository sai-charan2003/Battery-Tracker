package com.example.battery_tracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.battery_tracker.widgets.material3.Material3widget
import com.example.battery_tracker.widgets.transparent.TransparentWidget
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhoneListenerService: WearableListenerService() {




    val scope = CoroutineScope(Dispatchers.IO)
    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(TAG, String(messageEvent.data))

    }


    override fun onCreate() {




        super.onCreate()
        val battery= getBatteryPercentage(applicationContext)
        val stringbattery=battery.toString()
        val devicename= Build.DEVICE


        val batteryIntent =
            applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
        val ischarging= ischargingfun(chargingstatus).toString()
        val ouput=stringbattery+"ischarging"+ischarging

        scope.launch(Dispatchers.IO) {

            getNodes(applicationContext).forEach { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    "/deploy",
                    ouput.toByteArray()
                ).apply {
                    addOnSuccessListener {

                    }
                    addOnFailureListener {  }
                }



            }.toString()
            Material3widget.updateAll(applicationContext)
            TransparentWidget.updateAll(applicationContext)

        }

    }



    companion object{
        private const val TAG = "PhoneListenerService"
        private const val MESSAGE_PATH = "/deploy"
    }
}
private fun getNodes(context: Context): Collection<String> {

    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.id }
}

private fun getBatteryPercentage(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }

    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

    return if (level == -1 || scale == -1) {
        0
    } else {
        // Calculate the battery percentage
        (level.toFloat() / scale.toFloat() * 100).toInt()
    }
}
fun ischargingfun(charging:Int):Boolean{

    var ischarging=false
    when(charging){
        BatteryManager.BATTERY_STATUS_CHARGING->ischarging=true

    }
    return ischarging

}