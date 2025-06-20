package dev.charan.batteryTracker.data.repository

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.data.prefs.SharedPref
import dev.charan.batteryTracker.utils.NotificationHelper
import dev.charan.batteryTracker.utils.SettingsUtils
import dev.charan.batteryTracker.widgets.Material3widget
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
    val settingsUtils : SettingsUtils,
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
        if(settingsUtils.isBluetoothPermissionGranted()){
            batteryInfoRepo.registerBluetoothBatteryReceiver()
            batteryInfoRepo.registerWearOsBatteryReceiver()
        }
        batteryInfoRepo.registerBatteryReceiver()



    }

    fun batteryData() : BatteryInfo =
         batteryInfoRepo.getPhoneBatteryData()


    fun bluetoothBatteryData() : Flow<BluetoothDeviceBatteryInfo?> =
        batteryInfoRepo.getBluetoothBatteryDetails()

    suspend fun allDevicesBatteryData(): WidgetState {
        if(settingsUtils.isBluetoothPermissionGranted()) {
            batteryInfoRepo.sendSignalToWearOs()
        }
        return WidgetState(
            deviceBattery = batteryInfoRepo.getPhoneBatteryData(),
            bluetoothBattery = batteryInfoRepo.getBluetoothBatteryDetails().first() ?: BluetoothDeviceBatteryInfo()
        )
    }




    fun cleanUp() {
        batteryInfoRepo.unRegisterBatteryReceiver()
    }

    suspend fun updateWidget() {
        Material3widget.updateAll(context)


    }



}