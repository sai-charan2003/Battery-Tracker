package dev.charan.batteryTracker.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.charan.batteryTracker.R

@Composable
@GlanceComposable
fun IconBatteryWidgetView( batteryLevel: Int) {
    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            provider = ImageProvider(R.drawable.vector),
            null,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier
                .padding(start = 10.dp, top = 10.dp)
                .size(43.dp),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)

        )
        LinearProgressIndicator(
             batteryLevel/100f,
            modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp),
            color = ColorProvider(
                Color.Green), backgroundColor = ColorProvider(Color.LightGray)
        )
        Text(batteryLevel.toString(), style = TextStyle(color = GlanceTheme.colors.onSurface))
    }

}