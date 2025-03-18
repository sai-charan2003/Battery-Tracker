package dev.charan.batteryTracker.widgets

import android.appwidget.AppWidgetManager
import android.content.Context

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent

import androidx.glance.background
import androidx.glance.currentState

import androidx.glance.layout.fillMaxSize

import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.android.AndroidEntryPoint


import dev.charan.batteryTracker.data.Repository.WidgetRepository
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.data.worker.BatteryWidgetUpdateWorker
import dev.charan.batteryTracker.widgets.components.WidgetContent
import javax.inject.Inject


object Material3widget: GlanceAppWidget() {
    @RequiresApi(Build.VERSION_CODES.R)


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = WidgetRepository.get(context)
        provideContent {
            GlanceTheme {
                Log.d("TAG", "provideGlance: updated")
                Material3WidgetContent(repo)
            }
        }
    }

    @AndroidEntryPoint
    class Material3WidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget
            get() = Material3widget
        @Inject
        lateinit var  widgetRepository : WidgetRepository

        override fun onDisabled(context: Context?) {
            widgetRepository.cleanUp()
        }

        override fun onEnabled(context: Context?) {
            super.onEnabled(context)
            widgetRepository.startObserving()
            BatteryWidgetUpdateWorker.setup(context!!)

        }

    }


    @Composable
    fun Material3WidgetContent(
        widgetRepository: WidgetRepository
    ) {

        val batteryState by widgetRepository.allDevicesBatteryData().collectAsState(null)
        Log.d("TAG", "Material3WidgetContent: $batteryState")
        Scaffold(
            titleBar = {
                Text(
                    text = "Battery Tracker",
                    modifier = GlanceModifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 15.dp,
                        top = 15.dp
                    ),
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
                .fillMaxSize()


        ) {
            WidgetContent(
                phoneBatteryState = batteryState?.deviceBattery ?: BatteryInfo(),
                bluetoothBatteryState = batteryState?.bluetoothBattery ?: BluetoothDeviceBatteryInfo(),
                GlanceModifier.background(GlanceTheme.colors.surface)
                )
        }
    }
}

