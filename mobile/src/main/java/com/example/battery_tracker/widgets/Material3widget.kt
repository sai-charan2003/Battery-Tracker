package com.example.battery_tracker.widgets.material3

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BATTERY_CHANGED
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.battery_tracker.R
import com.example.battery_tracker.Screens.getNodes
import com.example.battery_tracker.Screens.getnodenames
import com.example.battery_tracker.widgets.transparent.transparent

import com.example.battery_tracker.workmanager
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


object Material3widget: GlanceAppWidget() {
    var batterylevel = intPreferencesKey("count")
    var headphonebattery= intPreferencesKey("count1")
    var realheadphonebattery= intPreferencesKey("maincount")
    val bluecount= intPreferencesKey("count2")
    val headphonename= stringPreferencesKey("count3")
    val ischarging= booleanPreferencesKey("ischarging")
    val islowpower= booleanPreferencesKey("islowpower")





    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {



//        val batterychange=batterychange()
//        val filter=IntentFilter(Intent.ACTION_BATTERY_CHANGED)
//        val change=IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
//
//        context.registerReceiver(batterychange,filter)
//        context.registerReceiver(batterychange,change)


        provideContent{
            GlanceTheme {
                LaunchedEffect(Unit) {
                    task(context)
                }


                val sharedpreferences:SharedPreferences= LocalContext.current.getSharedPreferences("Devicename",Context.MODE_PRIVATE)
                val devicename by remember {
                    mutableStateOf(sharedpreferences.getString("Devicename", Build.MODEL))
                }
                val scope= CoroutineScope(Dispatchers.IO)

                var headphonebattery= currentState(key = headphonebattery)?:0
                var ischarging= currentState(key= ischarging)
                var islowpower= currentState(key= islowpower)
                var bluecount= currentState(key= bluecount)?:0
                var realheadphonebattery= currentState(key= realheadphonebattery)?:0
                var headphonename= currentState(key=headphonename)
                bluecount=0
                val batteryIntent =
                    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
                if (powerManager != null) {
                    islowpower=powerManager.isPowerSaveMode
                }
                var wearosbattery by remember {
                    mutableStateOf("")

                }


                var wearos :
                    MutableMap<String,Int> = mutableMapOf()



                Wearable.getMessageClient(context).addListener {

                    wearosbattery=String(it.data)

                }

                var wearosname by remember {
                    mutableStateOf("")
                }

                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (ActivityCompat.checkSelfPermission(LocalContext.current ,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                ) {


                    val pariedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                    pariedDevice.forEach{
                        headphonebattery= it?.let { bluetoothDevice ->
                            (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                                .invoke(it) as Int

                        }?:-1
                        if(headphonebattery!=-1){
                            headphonename=it.alias
                            Log.d("TAG", "provideGlance: $headphonename")
                            bluecount= bluecount+1
                            realheadphonebattery=headphonebattery
                        }
                        val bluetoothClass=it.bluetoothClass
                        if(bluetoothClass!=null && bluetoothClass.majorDeviceClass==BluetoothClass.Device.Major.WEARABLE){
                            val uuids = it.uuids
                            // Check UUIDs here if needed
                            Log.d("TAG", "WearOS device found: ${it.alias}")
                            wearosname= it.alias.toString()
                            scope.launch(Dispatchers.IO) {
                                val text = ""
                                var transcriptionNodeId: String? = null
                                transcriptionNodeId = getNodes(context).forEach { nodeId ->
                                    Wearable.getMessageClient(context).sendMessage(
                                        nodeId,
                                        "/deploy",
                                        text.toByteArray()
                                    ).apply {
                                        addOnSuccessListener { Log.d("TAG", "OnSuccess") }
                                        addOnFailureListener { Log.d("TAG", "OnFailure") }
                                    }


                                }.toString()
                                Log.d("TAG", "onCreate: ${getNodes(context)}")
                            }

                        }





                    }



                }
                var batteryLevel = currentState(key = batterylevel) ?: 0


                val batteryManager = LocalContext.current.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                ischarging= ischargingfun(chargingstatus)

                val blutooth:BluetoothHeadset?=null
//                context.registerReceiver(updatewidget(),
//                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
//                )
                val batteryStatusIntent = Intent(ACTION_BATTERY_CHANGED)

                Column(

                    modifier = GlanceModifier.fillMaxWidth().background(GlanceTheme.colors.surface).padding(end=8.dp, start = 8.dp).clickable(
                        actionRunCallback(IncrementActionCallback::class.java)
                    ),

                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {



                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 25.dp,),

                        ) {

                        devicename?.let {
                            Text(text = it, style = TextStyle(
                                color=GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold,
                            ), modifier=GlanceModifier.padding(top = 6.dp))
                        }


                        Spacer(GlanceModifier.defaultWeight())
                        if(ischarging==true){
                            Image(provider = ImageProvider(R.drawable.charging), contentDescription = null, modifier = GlanceModifier.padding(top = 8.dp, end = 8.dp))
                        }


                        if (powerManager != null) {
                            if(islowpower == true){

                                LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Yellow), backgroundColor = ColorProvider(Color.LightGray))
                            } else {
                                LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                            }
                        }

                        Text(


                            text = "$batteryLevel%",
                            style = TextStyle(
                                color=GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold




                                )
                        )
                    }



                    if(wearosbattery!="") {


                        Row(modifier = GlanceModifier.fillMaxSize()) {

                            Text(
                                text = wearosname,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold


                                ),
                                modifier = GlanceModifier.padding(top = 5.dp)
                            )
                            Spacer(GlanceModifier.defaultWeight())
                            if(wearosbattery.substringAfter("ischarging").toBoolean()==true){
                                Image(provider = ImageProvider(R.drawable.charging), contentDescription = null, modifier = GlanceModifier.padding(top = 8.dp, end = 8.dp))
                            }



                            if (wearosbattery.substringBefore("ischarging").toInt() >= 21) {

                                LinearProgressIndicator(wearosbattery.substringBefore("ischarging").toInt() / 100f,
                                    modifier = GlanceModifier.fillMaxHeight()
                                        .padding(end = 15.dp, top = 12.dp).size(100.dp)
                                        .height(20.dp),
                                    color = ColorProvider(Color.Green),
                                    backgroundColor = ColorProvider(Color.LightGray)
                                )
                            } else if (wearosbattery.substringBefore("ischarging").toInt() <= 50) {
                                LinearProgressIndicator(wearosbattery.substringBefore("ischarging").toInt() / 100f,
                                    modifier = GlanceModifier.fillMaxHeight()
                                        .padding(end = 15.dp, top = 12.dp).size(100.dp)
                                        .height(20.dp),
                                    color = ColorProvider(Color.Yellow),
                                    backgroundColor = ColorProvider(Color.LightGray)
                                )
                            } else if (wearosbattery.substringBefore("ischarging").toInt() <= 20) {
                                LinearProgressIndicator(wearosbattery.substringBefore("ischarging").toInt() / 100f,
                                    modifier = GlanceModifier.fillMaxHeight()
                                        .padding(end = 15.dp, top = 12.dp).size(100.dp)
                                        .height(20.dp),
                                    color = ColorProvider(Color.Red),
                                    backgroundColor = ColorProvider(Color.LightGray)
                                )
                            }


                            Text(
                                text = wearosbattery.substringBefore("ischarging") + "%",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold


                                ),
                                modifier = GlanceModifier.padding(top = 5.dp)
                            )


                        }
                    }




                    Row (modifier = GlanceModifier.fillMaxSize()){
                        if(realheadphonebattery!=0){
                            Text(
                                text = headphonename.toString(),
                                style = TextStyle(
                                    color=GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold






                                    ),
                                modifier = GlanceModifier.padding(top = 5.dp)
                            )
                            Spacer(GlanceModifier.defaultWeight())


                            if(realheadphonebattery>=21){

                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                            }
                            else if(realheadphonebattery<=50){
                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Yellow), backgroundColor = ColorProvider(Color.LightGray))
                            }
                            else if(realheadphonebattery<=20){
                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Red), backgroundColor = ColorProvider(Color.LightGray))
                            }


                            Text(text =realheadphonebattery.toString()+"%"
                                ,
                                style = TextStyle(
                                    color=GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold




                                    ),
                                modifier = GlanceModifier.padding(top = 5.dp)
                                )

                        }
                    }
                }
            }
        }
    }
}
class SimpleCounterWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = Material3widget
}

class IncrementActionCallback: ActionCallback {

    var headphonebattery:Int = 0
    var realheadphonebattery:Int=0
    var devices:Int = 0


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

            updateAppWidgetState(context, glanceId) { prefs ->
            val currentCount = prefs[Material3widget.batterylevel]
            if(currentCount != null) {
                prefs[Material3widget.batterylevel] = BatteryManager.BATTERY_PROPERTY_CAPACITY
            }
            val batteryIntent =
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status:Int=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
                val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
                if (powerManager != null) {
                    prefs[Material3widget.islowpower]=powerManager.isPowerSaveMode
                }
            prefs[Material3widget.ischarging]=ischargingfun(status)
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (ActivityCompat.checkSelfPermission(context ,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ) {
                val pariedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                pariedDevice.forEach{
                    headphonebattery= it?.let { bluetoothDevice ->
                        (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                            .invoke(it) as Int
                    }!!
                    if(headphonebattery!=-1){
                        prefs[Material3widget.headphonename]=it.name

                        devices += 1
                        realheadphonebattery=headphonebattery
                        prefs[Material3widget.realheadphonebattery]=realheadphonebattery



                    }
                    if(devices==0){
                        prefs[Material3widget.realheadphonebattery]=0
                    }




                }



            }

        }
        Material3widget.update(context, glanceId)



    }
}
fun ischargingfun(charging:Int):Boolean{

    var ischarging=false
    when(charging){
        BatteryManager.BATTERY_STATUS_CHARGING->ischarging=true

    }
    return ischarging

}


class batterychange:BroadcastReceiver(){

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent?) {

        GlobalScope.launch {
            val manager = GlanceAppWidgetManager(context)
            val glanceids=manager.getGlanceIds(Material3widget::class.java).forEach {
                var headphonebattery:Int = 0
                var realheadphonebattery:Int=0
                var devices:Int = 0
                updateAppWidgetState(context,it){
                        prefs ->
                    val currentCount = prefs[Material3widget.batterylevel]
                    if(currentCount != null) {
                        prefs[Material3widget.batterylevel] = BatteryManager.BATTERY_PROPERTY_CAPACITY
                    }
                    val batteryIntent =
                        context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    val status:Int=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
                    val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
                    if (powerManager != null) {
                        prefs[Material3widget.islowpower]=powerManager.isPowerSaveMode
                    }
                    prefs[Material3widget.ischarging]=ischargingfun(status)
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (ActivityCompat.checkSelfPermission(context ,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val pariedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                        pariedDevice.forEach{
                            headphonebattery= it?.let { bluetoothDevice ->
                                (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                                    .invoke(it) as Int
                            }!!
                            if(headphonebattery!=-1){
                                prefs[Material3widget.headphonename]=it.name

                                devices += 1
                                realheadphonebattery=headphonebattery
                                prefs[Material3widget.realheadphonebattery]=realheadphonebattery



                            }
                            if(devices==0){
                                prefs[Material3widget.realheadphonebattery]=0
                            }




                        }



                    }
                }
            }
            Log.d("TAG", "onReceive: hi")
            Material3widget.updateAll(context)
            transparent.updateAll(context)

        }
    }

}
fun task(context:Context){

    val batteryStatusRequest = PeriodicWorkRequestBuilder<workmanager>(
        repeatInterval = 15,
        repeatIntervalTimeUnit = TimeUnit.MINUTES
    )

        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "BatteryStatusWorker",
        ExistingPeriodicWorkPolicy.UPDATE,
        batteryStatusRequest
    )

}





















