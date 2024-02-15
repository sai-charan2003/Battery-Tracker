package com.example.battery_tracker.widgets.transparent

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
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

import com.example.battery_tracker.R
import com.example.battery_tracker.Screens.getNodes
import com.example.battery_tracker.widgets.material3.IncrementActionCallback
import com.example.battery_tracker.widgets.material3.Material3widget
import com.example.battery_tracker.widgets.material3.batterychange
import com.example.battery_tracker.widgets.material3.ischargingfun
import com.example.battery_tracker.widgets.material3.task
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log


object transparent: GlanceAppWidget() {




    var batterylevel = intPreferencesKey("count")
    var headphonebattery= intPreferencesKey("count1")
    var realheadphonebattery= intPreferencesKey("maincount")
    val bluecount= intPreferencesKey("count2")
    val headphonename= stringPreferencesKey("count3")
    val ischarging= booleanPreferencesKey("ischarging")
    val islowpower= booleanPreferencesKey("islowpower")

    var wearosdevice by mutableStateOf(false)
    var wearosbattery by
        mutableStateOf("")







    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        val batterychange= batterychange()
        val filter=IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val change=IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)

        context.registerReceiver(batterychange,filter)
        context.registerReceiver(batterychange,change)




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



                var headphonebattery= currentState(key = Material3widget.headphonebattery)?:0
                var ischarging= currentState(key= Material3widget.ischarging)
                var islowpower= currentState(key= Material3widget.islowpower)
                var bluecount= currentState(key= Material3widget.bluecount)?:0
                var realheadphonebattery= currentState(key= Material3widget.realheadphonebattery)?:0
                var headphonename= currentState(key= Material3widget.headphonename)
                bluecount=0
                val batteryIntent =
                    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
                if (powerManager != null) {
                    islowpower=powerManager.isPowerSaveMode
                }

                var wearos :
                        MutableMap<String,Int> = mutableMapOf()



                Wearable.getMessageClient(context).addListener {
                    Log.d("TAG", String(it.data))

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
                        if(bluetoothClass!=null && bluetoothClass.majorDeviceClass== BluetoothClass.Device.Major.WEARABLE){
                            val uuids = it.uuids
                            // Check UUIDs here if needed
                            wearosdevice=true


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
                var batteryLevel = currentState(key = Material3widget.batterylevel) ?: 0


                val batteryManager = LocalContext.current.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                ischarging= ischargingfun(chargingstatus)

                val blutooth:BluetoothHeadset?=null
//                context.registerReceiver(updatewidget(),
//                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
//                )
                val batteryStatusIntent = Intent(Intent.ACTION_BATTERY_CHANGED)

                Column(

                    modifier = GlanceModifier.fillMaxWidth().padding(end=8.dp, start = 8.dp).clickable(
                        actionRunCallback(Transparentaction::class.java)
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




class transparentReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = transparent
}


class Transparentaction: ActionCallback {

    var headphonebattery:Int = 0
    var realheadphonebattery:Int=0
    var devices:Int = 0


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {





        updateAppWidgetState(context, glanceId) { prefs ->
            val currentCount = prefs[transparent.batterylevel]
            val ischarging=prefs[transparent.ischarging]




            if(currentCount != null) {
                prefs[transparent.batterylevel] = BatteryManager.BATTERY_PROPERTY_CAPACITY
            }
            val batteryIntent =
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))


            val status:Int=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
            val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
            if (powerManager != null) {
                prefs[transparent.islowpower]=powerManager.isPowerSaveMode
            }
            prefs[transparent.ischarging]=ischargingfun(status)

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
                        prefs[transparent.headphonename]=it.name

                        devices += 1
                        realheadphonebattery=headphonebattery
                        prefs[transparent.realheadphonebattery]=realheadphonebattery



                    }
                    if(devices==0){
                        prefs[transparent.realheadphonebattery]=0
                    }




                }



            }

        }
        transparent.update(context, glanceId)



    }
}


















