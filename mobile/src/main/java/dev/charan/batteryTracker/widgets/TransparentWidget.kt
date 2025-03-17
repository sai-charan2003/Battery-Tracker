package dev.charan.batteryTracker.widgets

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import dagger.hilt.android.AndroidEntryPoint
import dev.charan.batteryTracker.data.Repository.WidgetRepository
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.data.model.BluetoothDeviceBatteryInfo
import dev.charan.batteryTracker.widgets.components.WidgetContent
import javax.inject.Inject


object TransparentWidget: GlanceAppWidget() {
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = WidgetRepository.get(context)
        provideContent {
            GlanceTheme {
                TransparentWidgetContent(repo)

            }
        }
    }
}


@AndroidEntryPoint
class TransparentReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TransparentWidget
    @Inject
    lateinit var  widgetRepository : WidgetRepository
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        widgetRepository.startObserving()
    }


    override fun onDisabled(context: Context?) {
        widgetRepository.cleanUp()
    }
}

@Composable
fun TransparentWidgetContent(
    widgetRepository: WidgetRepository

) {
    val phoneBatteryInfo by widgetRepository.batteryData().collectAsState(null)
    val headPhoneBatteryInfo by widgetRepository.bluetoothBatteryData().collectAsState(null)

    WidgetContent(
        phoneBatteryState = phoneBatteryInfo ?: BatteryInfo(),
        bluetoothBatteryState = headPhoneBatteryInfo ?: BluetoothDeviceBatteryInfo(),
    )


}




















