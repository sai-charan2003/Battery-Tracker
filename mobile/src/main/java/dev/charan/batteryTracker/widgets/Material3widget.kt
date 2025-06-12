package dev.charan.batteryTracker.widgets

import android.content.Context

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent

import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row

import androidx.glance.layout.fillMaxSize

import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint


import dev.charan.batteryTracker.data.repository.WidgetRepository
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.data.worker.BatteryWidgetUpdateWorker
import dev.charan.batteryTracker.widgets.components.WidgetContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


class Material3widget: GlanceAppWidget() {
    companion object {
        private val SMALL_LAYOUT = DpSize(150.dp, 100.dp)
        private val BIG_LAYOUT = DpSize(250.dp, 250.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_LAYOUT,
            BIG_LAYOUT
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = WidgetRepository.get(context)
        provideContent {
            GlanceTheme {
                Material3WidgetContent(repo)
            }
        }
    }

    @Composable
    fun Material3WidgetContent(
        widgetRepository: WidgetRepository
    ) {
        val size = LocalSize.current
        var batteryState by remember {
            mutableStateOf(WidgetState())
        }
        val bluetoothDeviceBatteryInfo by widgetRepository.bluetoothBatteryData()
            .collectAsState(BluetoothDeviceBatteryInfo())
        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                batteryState = widgetRepository.allDevicesBatteryData()

            }
        }


        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(5.dp)
                .cornerRadius(12.dp)
        ) {
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
            WidgetContent(
                phoneBatteryState = batteryState?.deviceBattery ?: BatteryInfo(),
                bluetoothBatteryState = bluetoothDeviceBatteryInfo ?: BluetoothDeviceBatteryInfo(),
                modifier = GlanceModifier.background(GlanceTheme.colors.surface),
                isLargeWidget = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    size.width >= BIG_LAYOUT.width
                } else {
                    true
                }
            )



        }

    }
}

    @AndroidEntryPoint
    class Material3WidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget
            get() = Material3widget()


//        override fun onDisabled(context: Context?) {
//            widgetRepository.cleanUp()
//        }

        override fun onEnabled(context: Context?) {
            super.onEnabled(context)
            BatteryWidgetUpdateWorker.setup(context!!)

        }

    }


