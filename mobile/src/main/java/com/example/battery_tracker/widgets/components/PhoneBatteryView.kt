package com.example.battery_tracker.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.battery_tracker.R

@Composable
@GlanceComposable
fun DeviceBatteryView(deviceName:String,deviceBattery:Int,isCharging:Boolean,isLowPowerMode:Boolean,modifier: GlanceModifier){
    Row(
        modifier = GlanceModifier.fillMaxWidth().then(modifier),

        ) {

        Text(text = deviceName, style = TextStyle(
            color= GlanceTheme.colors.onSurface,
            fontWeight = FontWeight.Bold,
        ), modifier= GlanceModifier.padding(top = 6.dp))


        Spacer(GlanceModifier.defaultWeight())

        if(isCharging){
            Image(
                provider = ImageProvider(R.drawable.charging),
                contentDescription = null,
                modifier = GlanceModifier.padding(top = 8.dp, end = 8.dp))
        }



            if(isLowPowerMode){

                LinearProgressIndicator(
                    deviceBattery/100f,
                    modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp),
                    color = ColorProvider(
                    Color.Yellow), backgroundColor = ColorProvider(Color.LightGray)
                )
            } else {
                LinearProgressIndicator(
                    deviceBattery/100f,
                    modifier = GlanceModifier.fillMaxHeight().padding(end = 15.dp, top = 12.dp).size(100.dp).height(21.dp),
                    color = ColorProvider(
                    Color.Green), backgroundColor = ColorProvider(Color.LightGray)
                )
            }


        Text(
            text = "${deviceBattery}%",
            style = TextStyle(
                color= GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
