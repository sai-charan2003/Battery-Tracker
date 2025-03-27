package dev.charan.batteryTracker.data.repository

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.data.prefs.SharedPref
import dev.charan.batteryTracker.utils.NotificationHelper
import dev.charan.batteryTracker.widgets.Material3widget
import dev.charan.batteryTracker.widgets.TransparentWidget
import dev.charan.batteryTracker.widgets.WidgetState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepository @Inject constructor(
    val batteryInfoRepo: BatteryInfoRepo,
    val sharedPref: SharedPref,
    @ApplicationContext val context : Context,
) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetRepositoryEntryPoint {
        fun widgetModelRepository(): WidgetRepository
    }
    init {
        startObserving()
    }

    companion object {
        fun get(applicationContext: Context): WidgetRepository {
            val widgetRepositoryEntryPoint: WidgetRepositoryEntryPoint = EntryPoints.get(
                applicationContext,
                WidgetRepositoryEntryPoint::class.java
            )
            return widgetRepositoryEntryPoint.widgetModelRepository()
        }
    }

    fun startObserving() {
        batteryInfoRepo.registerBatteryReceiver()
        batteryInfoRepo.registerWearOsBatteryReceiver()
        batteryInfoRepo.registerBluetoothBatteryReceiver()

    }

    fun batteryData() : BatteryInfo =
         batteryInfoRepo.getPhoneBatteryData()


    fun bluetoothBatteryData() : Flow<BluetoothDeviceBatteryInfo?> =
        batteryInfoRepo.getBluetoothBatteryDetails()

    suspend fun allDevicesBatteryData(): WidgetState {
        batteryInfoRepo.sendSignalToWearOs()
        return WidgetState(
            deviceBattery = batteryInfoRepo.getPhoneBatteryData(),
            bluetoothBattery = batteryInfoRepo.getBluetoothBatteryDetails().first() ?: BluetoothDeviceBatteryInfo()
        )
    }




    fun cleanUp() {
        batteryInfoRepo.unRegisterBatteryReceiver()
    }

    suspend fun updateWidget() {
        val manager = GlanceAppWidgetManager(context)
        val widgetIds = manager.getGlanceIds(Material3widget::class.java)
        widgetIds.forEach {
            Material3widget.update(context, it)
            TransparentWidget.update(context, it)
        }


    }



}