package com.plcoding.widgetswithcompose


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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
import com.example.battery_widget.R



object transparent: GlanceAppWidget() {




    var batterylevel = intPreferencesKey("count")
    var headphonebattery= intPreferencesKey("count1")
    var realheadphonebattery= intPreferencesKey("maincount")
    val bluecount= intPreferencesKey("count2")
    val headphonename= stringPreferencesKey("count3")
    val ischarging= booleanPreferencesKey("ischarging")
    val islowpower= booleanPreferencesKey("islowpower")




    override suspend fun provideGlance(context: Context,glanceId: GlanceId) {



        provideContent{
            GlanceTheme {
                val sharedPreferences:SharedPreferences=context.getSharedPreferences("Devicename",Context.MODE_PRIVATE)
                val sharedPreferencesheadphones:SharedPreferences=context.getSharedPreferences("headphonesnames",Context.MODE_PRIVATE)
                val editor=sharedPreferences.edit()
                val editorearphones=sharedPreferencesheadphones.edit()
                editor.putString("Devicename",android.os.Build.MODEL)

                val devicename by remember {
                    mutableStateOf(sharedPreferences.getString("Devicename",android.os.Build.MODEL))
                }



                var headphonebattery= currentState(key = headphonebattery)?:0
                var ischarging= currentState(key= ischarging)
                var bluecount= currentState(key= bluecount)?:0
                var realheadphonebattery= currentState(key= realheadphonebattery)?:0
                var headphonename= currentState(key=headphonename)
                var islowpower= currentState(key= islowpower)


                bluecount=0


                val batteryIntent =
                    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
                val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)



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
                            headphonename=it.name

                            bluecount= bluecount+1
                            realheadphonebattery=headphonebattery
                        }




                    }



                }
                var batteryLevel = currentState(key = batterylevel) ?: 0


                val batteryManager = LocalContext.current.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                //batteryLevel=viewmodel.batterylevel
                ischarging= ischargingfun(chargingstatus)
                val batterylevel2 = remember {
                    mutableStateOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                }

                val blutooth:BluetoothHeadset?=null








                Column(

                    modifier = GlanceModifier.fillMaxWidth().padding(end=8.dp, start = 8.dp).clickable(
                        actionRunCallback(Transparentaction::class.java)
                    ),

                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {

                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 25.dp),

                        ) {
                        devicename?.let {
                            Text(text = it, style = TextStyle(
                                color=GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold


                            ),modifier = GlanceModifier.padding(top = 6.dp))
                        }


                        Spacer(GlanceModifier.defaultWeight())
                        if(ischarging==true){
                            Image(provider = ImageProvider(R.drawable.charging), contentDescription = null, modifier = GlanceModifier.padding(top = 8.dp, end = 8.dp))
                        }


                        if(islowpower == true){

                            LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Yellow), backgroundColor = ColorProvider(Color.LightGray))
                        }
                        else {
                            LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                        }


                        Text(
                            text = "${batterylevel2.value}%",
                            style = TextStyle(
                                color=GlanceTheme.colors.onSurface,


                                fontWeight = FontWeight.Bold

                                ),modifier = GlanceModifier.padding(top = 5.dp)
                        )
                    }


                    Row (modifier = GlanceModifier.fillMaxSize()){
                        if(realheadphonebattery!=0){
                            Text(
                                text = headphonename.toString(),
                                style = TextStyle(
                                    color=GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold


                                    ),
                                modifier = GlanceModifier.padding(top = 6.dp)
                            )
                            Spacer(GlanceModifier.defaultWeight())


                            if(realheadphonebattery>=50){

                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                            }
                            else if(realheadphonebattery<=50){
                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                            }
                            else if(realheadphonebattery<=20){
                                LinearProgressIndicator(realheadphonebattery/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(20.dp), color = ColorProvider(Color.Yellow), backgroundColor = ColorProvider(Color.LightGray))
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
                prefs[CounterWidget.islowpower]=powerManager.isPowerSaveMode
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


















