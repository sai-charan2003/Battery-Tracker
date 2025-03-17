package dev.charan.batteryTracker.data.Repository

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
import dev.charan.batteryTracker.widgets.Material3widget
import dev.charan.batteryTracker.widgets.TransparentWidget
import kotlinx.coroutines.flow.Flow
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

    fun batteryData() : Flow<BatteryInfo?> =
         batteryInfoRepo.getBatteryDetails()


    fun bluetoothBatteryData() : Flow<BluetoothDeviceBatteryInfo?> =
        batteryInfoRepo.getBluetoothBatteryDetails()



    fun cleanUp() {
        batteryInfoRepo.unRegisterBatteryReceiver()
    }

    suspend fun updateWidget() {
        Log.d("TAG", "updateWidget: updated widget")
        val manager = GlanceAppWidgetManager(context)
        val widgetIds = manager.getGlanceIds(Material3widget::class.java)
        widgetIds.forEach {
            Material3widget.update(context, it)
            TransparentWidget.update(context, it)
        }


    }



}