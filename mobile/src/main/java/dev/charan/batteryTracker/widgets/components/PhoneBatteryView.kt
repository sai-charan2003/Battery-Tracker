package dev.charan.batteryTracker.widgets.components

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.charan.batteryTracker.R

@SuppressLint("RestrictedApi")
@Composable
@GlanceComposable
fun DeviceBatteryView(
    deviceName: String,
    deviceBattery: String,
    batteryPercentage: Float,
    isCharging: Boolean,
    isLowPowerMode: Boolean,
    isLargeWidget: Boolean,
    deviceIcon: ImageProvider,
    modifier: GlanceModifier
) {
    val batteryColor = when {
        isLowPowerMode -> Color.Yellow
        else -> Color.Green
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Image(
            provider = deviceIcon,
            contentDescription = null,
            modifier = GlanceModifier.size(26.dp).padding(end = 8.dp),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
        )
        if (isLargeWidget) {
            Text(
                text = deviceName,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }
        if(!isLargeWidget){
            Spacer(modifier = GlanceModifier.defaultWeight())
        }


        if (isCharging) {
            Image(
                provider = ImageProvider(R.drawable.charging),
                contentDescription = "Charging",
                modifier = GlanceModifier.size(16.dp).padding(end = 8.dp)
            )
        }



        LinearProgressIndicator(
            progress = batteryPercentage,
            modifier = GlanceModifier
                .height(8.dp)
                .width(60.dp)
                .padding(end = 8.dp),
            color = ColorProvider(batteryColor),
            backgroundColor = ColorProvider(Color.LightGray)
        )



        Text(
            text = "$deviceBattery%",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

