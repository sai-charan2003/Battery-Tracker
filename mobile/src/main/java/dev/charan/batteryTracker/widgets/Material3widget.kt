package dev.charan.batteryTracker.widgets

import android.content.Context

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent

import androidx.glance.appwidget.updateAll
import androidx.glance.background

import androidx.glance.layout.Alignment
import androidx.glance.layout.Column

import androidx.glance.layout.fillMaxSize

import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.charan.batteryTracker.Utils.AppConstants

import dev.charan.batteryTracker.viewModel


import dev.charan.batteryTracker.Utils.BatteryWidgetUpdateWorker
import dev.charan.batteryTracker.Utils.GetBatteryDetails
import dev.charan.batteryTracker.Utils.SharedPref
import com.google.android.gms.wearable.Wearable
import dev.charan.batteryTracker.Utils.GetBatteryDetails.showLowBatteryNotificationForHeadPhones
import dev.charan.batteryTracker.widgets.components.DeviceBatteryView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object Material3widget: GlanceAppWidget() {
    var wearosString by
    mutableStateOf("null")
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.R)

    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        provideContent{
            GlanceTheme {
                LaunchedEffect(Unit) {
                    BatteryWidgetUpdateWorker.setup(context)
                }
                val viewModel= viewModel(context)
                viewModel.phoneBattery(context)
                viewModel.bluetoothBattery(context)
                val sharedPref = SharedPref.getInstance(context)

                val deviceName by remember {
                    mutableStateOf(sharedPref.deviceName)
                }

                val phoneBattery = viewModel.batterylevel
                val isPhoneCharging = viewModel.ischargingstatus
                val isPhoneLowPowerMode = viewModel.islowpower
                val wearosName=viewModel.wearosName

                Wearable.getMessageClient(context).addListener {
                    wearosString = String(it.data)
                }
                val wearosBattery = wearosString.substringBefore(AppConstants.WEAROS_CHARGING_DIVIDER)
                Log.d("TAG", "provideGlance: $wearosBattery")
                val isWearosCharging = wearosString.substringAfter(AppConstants.WEAROS_CHARGING_DIVIDER).toBoolean()
                if(wearosString != "null" && wearosName!="null") {
                    Log.d("TAG", "provideGlance: sending notification")
                    if (wearosBattery <= sharedPref.minWearosBattery.toString() && wearosBattery!="100") {
                        if (!sharedPref.isNotificationSent) {

                            GetBatteryDetails.showLowBatteryNotification(
                                wearosName,
                                wearosBattery,
                                context
                            )
                        }
                    } else if (wearosBattery > sharedPref.minWearosBattery.toString()) {
                        sharedPref.isNotificationSent = false
                    }
                }


                val headphonesName = viewModel.headPhoneName
                val headphoneBattery = viewModel.bluetoothBattery
                if(headphoneBattery.isNotEmpty()) {
                    handleHeadphoneBatteryNotification(
                        headphonesName = headphonesName,
                        headphoneBattery = headphoneBattery,
                        context = context,
                        sharedPref = sharedPref

                    )

                }



                Scaffold(
                    titleBar = {
                        Text(text = "Battery Tracker",modifier=GlanceModifier.padding(start = 10.dp,end=10.dp,bottom=15.dp,top=15.dp),style = TextStyle(
                            color= GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),)
                    },
                    horizontalPadding = 0.dp,


                    modifier = GlanceModifier
                        .padding(start = 5.dp,end=5.dp,bottom=10.dp)
                        .cornerRadius(5.dp)
                        .clickable {
                            CoroutineScope(Dispatchers.IO).launch{
                                Material3widget.updateAll(context)

                            }
                        }
                        .fillMaxSize()



                ) {
                    Column(
                        modifier= GlanceModifier
                            .cornerRadius(25.dp)
                            .fillMaxSize()
                            .padding(start = 8.dp,end=8.dp, bottom = 5.dp, top = 15.dp)

                            .background(GlanceTheme.colors.surface),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        DeviceBatteryView(
                            deviceName = deviceName!!,
                            deviceBattery = phoneBattery,
                            isCharging = if (isPhoneCharging == "Charging") true else false,
                            isLowPowerMode = isPhoneLowPowerMode,
                            modifier = GlanceModifier.padding(bottom = 20.dp)

                        )

                        if (wearosString != "null" && wearosName!="null" && wearosBattery != "getbattery") {
                            DeviceBatteryView(
                                deviceName = wearosName,
                                deviceBattery = wearosBattery.toInt(),
                                isCharging = isWearosCharging,
                                isLowPowerMode = false,
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
    private fun handleHeadphoneBatteryNotification(
        headphonesName: String,
        headphoneBattery: String,
        context: Context,
        sharedPref: SharedPref
    ) {
        val batteryLevel = headphoneBattery.toIntOrNull() ?: return
        val minBatteryThreshold = sharedPref.minHeadphonesBattery.toString().toIntOrNull() ?: return

        when {

            batteryLevel <= minBatteryThreshold &&
                    batteryLevel != 100 &&
                    !sharedPref.isNotificationSentForHeadPhones -> {

                showLowBatteryNotificationForHeadPhones(
                    headphonesName = headphonesName,
                    batteryLevel = headphoneBattery,
                    context = context,
                    sharedPref = sharedPref
                )

                sharedPref.isNotificationSentForHeadPhones = true
            }


            batteryLevel > minBatteryThreshold -> {
                sharedPref.isNotificationSentForHeadPhones = false
            }


        }
    }
}
class Material3WidgetReceiver: GlanceAppWidgetReceiver() {
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
        Material3widget.updateAll(context)
    }
}





























