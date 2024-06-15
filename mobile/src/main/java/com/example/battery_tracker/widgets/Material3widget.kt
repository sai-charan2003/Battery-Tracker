package com.example.battery_tracker.widgets.material3

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.battery_tracker.viewModel
import com.example.battery_tracker.widgets.components.DeviceBatteryView
import com.example.battery_tracker.Utils.BatteryWidgetUpdateWorker
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Material3widget: GlanceAppWidget() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        provideContent{
            GlanceTheme {
                LaunchedEffect(Unit) {
                    BatteryWidgetUpdateWorker.setup()
                }

                val viewModel = viewModel(context)
                viewModel.phoneBattery(context)
                viewModel.bluetoothBattery(context)

                val sharedpreferences: SharedPreferences = LocalContext.current.getSharedPreferences("Devicename", Context.MODE_PRIVATE)
                val devicename by remember {
                    mutableStateOf(sharedpreferences.getString("Devicename", Build.MODEL))
                }

                val phoneBattery = viewModel.batterylevel
                val isPhoneCharging = viewModel.ischargingstatus
                val isPhoneLowPowerMode = viewModel.islowpower
                val wearosName = viewModel.wearosName

                var wearosBattery by remember { mutableStateOf("null") }
                var isWearosCharging by remember { mutableStateOf(false) }

                // Use LaunchedEffect to listen for Wear OS messages
                LaunchedEffect(context) {
                    Wearable.getMessageClient(context).addListener {
                        val wearosString = String(it.data)
                        Log.d("TAG", "inside: $wearosString")
                        wearosBattery = wearosString.substringBefore("ischarging")
                        isWearosCharging = wearosString.substringAfter("ischarging").toBoolean()
                    }
                }

                val headphonesName = viewModel.headPhoneName
                val headphoneBattery = viewModel.bluetoothBattery

                Scaffold(
                    titleBar = {
                        Text(
                            text = "Battery Tracker",
                            modifier = GlanceModifier.padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 15.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    },
                    horizontalPadding = 0.dp,
                    modifier = GlanceModifier
                        .padding(start = 5.dp, end = 5.dp, bottom = 10.dp)
                        .cornerRadius(5.dp)
                        .clickable {
                            CoroutineScope(Dispatchers.IO).launch {
                                Material3widget.updateAll(context)
                            }
                        }
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = GlanceModifier
                            .cornerRadius(25.dp)
                            .fillMaxSize()
                            .padding(start = 8.dp, end = 8.dp, bottom = 5.dp, top = 15.dp)
                            .background(GlanceTheme.colors.surface),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        DeviceBatteryView(
                            deviceName = devicename ?: "Unknown",
                            deviceBattery = phoneBattery,
                            isCharging = isPhoneCharging == "Charging",
                            isLowPowerMode = isPhoneLowPowerMode,
                            modifier = GlanceModifier.padding(bottom = 20.dp)
                        )

                        if (wearosBattery != "null" && wearosName != "null") {
                            DeviceBatteryView(
                                deviceName = wearosName,
                                deviceBattery = wearosBattery.toInt(),
                                isCharging = isWearosCharging,
                                isLowPowerMode = false,
                                modifier = GlanceModifier.padding(bottom = 20.dp)
                            )
                        }

                        if (headphoneBattery != "null") {
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
}

class Material3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = Material3widget
}

class IncrementActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Material3widget.updateAll(context)
    }
}
