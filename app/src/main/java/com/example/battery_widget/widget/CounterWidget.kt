package com.plcoding.widgetswithcompose


import android.Manifest
import android.bluetooth.BluetoothAdapter
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
import android.util.Log
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
import androidx.glance.appwidget.action.actionSendBroadcast
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
import com.example.battery_widget.R


object CounterWidget: GlanceAppWidget() {




    var batterylevel = intPreferencesKey("count")
    var headphonebattery= intPreferencesKey("count1")
    var realheadphonebattery= intPreferencesKey("maincount")
    val bluecount= intPreferencesKey("count2")
    val headphonename= stringPreferencesKey("count3")
    val ischarging= booleanPreferencesKey("ischarging")




    override suspend fun provideGlance(context: Context,glanceId: GlanceId) {



        provideContent{
            GlanceTheme {
                val sharedpreferences:SharedPreferences= LocalContext.current.getSharedPreferences("Devicename",Context.MODE_PRIVATE)
                val devicename by remember {
                    mutableStateOf(sharedpreferences.getString("Devicename", Build.MODEL))
                }









                var headphonebattery= currentState(key = headphonebattery)?:0
                var ischarging= currentState(key= ischarging)
                var bluecount= currentState(key= bluecount)?:0
                var realheadphonebattery= currentState(key= realheadphonebattery)?:0
                var headphonename= currentState(key=headphonename)
                bluecount=0


                val batteryIntent =
                    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
                Log.d("TAG", "provideGlance: $chargingstatus")
                Log.d("TAG", "provideGlance: ${ischargingfun(chargingstatus)}")


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
                ischarging= ischargingfun(chargingstatus)

                val blutooth:BluetoothHeadset?=null
//                context.registerReceiver(updatewidget(),
//                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
//                )
                val batteryStatusIntent = Intent(ACTION_BATTERY_CHANGED)


                Log.d("TAG", "provideGlance: ${Intent.ACTION_BATTERY_CHANGED}")


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


                        if(batteryLevel>=50){

                            LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                        }
                        else if(batteryLevel<=50){
                            LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Green), backgroundColor = ColorProvider(Color.LightGray))
                        }
                        else if(batteryLevel<=20){
                            LinearProgressIndicator(batteryLevel/100f, modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp), color = ColorProvider(Color.Yellow), backgroundColor = ColorProvider(Color.LightGray))
                        }

                        Text(


                            text = "$batteryLevel%",
                            style = TextStyle(
                                color=GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold




                                )
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
                                modifier = GlanceModifier.padding(top = 5.dp)
                            )
                            Spacer(GlanceModifier.defaultWeight())
                            Log.d("TAG", "provideGlance: ${ischargingfun(chargingstatus)}")

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
        get() = CounterWidget
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
            val currentCount = prefs[CounterWidget.batterylevel]
            if(currentCount != null) {
                prefs[CounterWidget.batterylevel] = BatteryManager.BATTERY_PROPERTY_CAPACITY
            }
            val batteryIntent =
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status:Int=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
            prefs[CounterWidget.ischarging]=ischargingfun(status)
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
                        prefs[CounterWidget.headphonename]=it.name

                        devices += 1
                        realheadphonebattery=headphonebattery
                        prefs[CounterWidget.realheadphonebattery]=realheadphonebattery


                        Log.d("TAG", "Content: ${prefs[CounterWidget.headphonebattery]}")
                    }
                    if(devices==0){
                        prefs[CounterWidget.realheadphonebattery]=0
                    }




                }



            }

        }
        CounterWidget.update(context, glanceId)



    }
}
fun ischargingfun(charging:Int):Boolean{
    Log.d("TAG", "ischargingfun: hi")
    var ischarging=false
    when(charging){
        BatteryManager.BATTERY_STATUS_CHARGING->ischarging=true

    }
    return ischarging

}
//class updatewidget: BroadcastReceiver(){
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//
//        actionRunCallback(IncrementActionCallback::class.java)
//    }
//}


















