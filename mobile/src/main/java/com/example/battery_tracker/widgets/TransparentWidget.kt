package com.example.battery_tracker.widgets.transparent

import android.content.Context
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
import com.example.battery_tracker.Utils.AppConstants
import com.example.battery_tracker.Utils.BatteryWidgetUpdateWorker
import com.example.battery_tracker.Utils.GetBatteryDetails
import com.example.battery_tracker.Utils.SharedPref

import com.example.battery_tracker.viewModel
import com.example.battery_tracker.widgets.components.DeviceBatteryView
import com.example.battery_tracker.widgets.material3.Material3widget
import com.google.android.gms.wearable.Wearable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object TransparentWidget: GlanceAppWidget() {
    var wearosString by
        mutableStateOf("null")

    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        provideContent{
            GlanceTheme {
                LaunchedEffect(Unit) {
                    BatteryWidgetUpdateWorker.setup(context)
                }
                val sharedPref = SharedPref.getInstance(context)
                val viewModel= viewModel(context)
                viewModel.phoneBattery(context)
                viewModel.bluetoothBattery(context)



                val deviceName by remember {
                    mutableStateOf(sharedPref.deviceName)
                }

                val phoneBattery = viewModel.batterylevel
                val isPhoneCharging = viewModel.ischargingstatus
                val isPhoneLowPowerMode = viewModel.islowpower
                val wearosName=viewModel.wearosName

                Wearable.getMessageClient(context).addListener {
                    Material3widget.wearosString = String(it.data)
                }
                val wearosBattery = Material3widget.wearosString.substringBefore(AppConstants.WEAROS_CHARGING_DIVIDER)
                val isWearosCharging = Material3widget.wearosString.substringAfter(AppConstants.WEAROS_CHARGING_DIVIDER).toBoolean()
                if(wearosBattery<= sharedPref.minWearosBattery.toString()){
                    if(!sharedPref.isNotificationSent) {
                        GetBatteryDetails.showLowBatteryNotification(wearosBattery, context)
                    }
                } else{
                    sharedPref.isNotificationSent=false
                }


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
                            deviceName = deviceName!!,
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




















