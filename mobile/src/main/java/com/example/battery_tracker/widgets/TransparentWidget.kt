package com.example.battery_tracker.widgets.transparent

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.example.battery_tracker.Utils.BatteryWidgetUpdateWorker

import com.example.battery_tracker.viewModel
import com.example.battery_tracker.widgets.components.DeviceBatteryView

import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object TransparentWidget: GlanceAppWidget() {








    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {






        provideContent{
            GlanceTheme {
                LaunchedEffect(Unit) {
                    BatteryWidgetUpdateWorker.setup()
                }
                val viewModel= viewModel(context)
                viewModel.phoneBattery(context)
                viewModel.bluetoothBattery(context)


                val sharedpreferences:SharedPreferences= LocalContext.current.getSharedPreferences("Devicename",Context.MODE_PRIVATE)
                val devicename by remember {
                    mutableStateOf(sharedpreferences.getString("Devicename", Build.MODEL))
                }

                val phoneBattery = viewModel.batterylevel
                val isPhoneCharging = viewModel.ischargingstatus
                val isPhoneLowPowerMode = viewModel.islowpower
                val wearosName=viewModel.wearosName
                var wearosString by remember {
                    mutableStateOf("null")
                }
                Wearable.getMessageClient(context).addListener {
                    wearosString = String(it.data)

                }
                val wearosBattery = wearosString.substringBefore("ischarging")
                val isWearosCharging = wearosString.substringAfter("ischarging").toBoolean()


                val headphonesName = viewModel.headPhoneName
                val headphoneBattery = viewModel.bluetoothBattery




                    Column(
                        modifier= GlanceModifier
                            .clickable {
                                CoroutineScope(Dispatchers.IO).launch{
                                    TransparentWidget.updateAll(context)

                                }
                            }

                            .cornerRadius(25.dp)
                            .fillMaxSize()
                            .padding(start = 8.dp,end=8.dp, bottom = 5.dp, top = 15.dp)

                            ,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        DeviceBatteryView(
                            deviceName = devicename!!,
                            deviceBattery = phoneBattery,
                            isCharging = if (isPhoneCharging == "Charging") true else false,
                            isLowPowerMode = isPhoneLowPowerMode,
                            modifier = GlanceModifier.padding(bottom = 20.dp)

                        )

                        if (wearosString != "null"&& wearosName!="null") {
                            DeviceBatteryView(
                                deviceName = wearosName,
                                deviceBattery = wearosBattery.toInt(),
                                isCharging = isWearosCharging,
                                isLowPowerMode = wearosString.substringBefore("ischarging").toInt()<=20,
                                modifier = GlanceModifier.padding(bottom = 20.dp)
                            )

                        }
                        if (headphoneBattery!="null") {
                            DeviceBatteryView(
                                deviceName = headphonesName,
                                deviceBattery = headphoneBattery.toInt(),
                                isCharging = false,
                                isLowPowerMode = false,
                                modifier = GlanceModifier
                            )
                        }

                    }

            }
        }
    }





}




class TransparentReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TransparentWidget
}




















