package dev.charan.batteryTracker.Screens.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TitleText(text:String,modifier: Modifier){
    Text(
        text = text,
        modifier = Modifier.then(modifier),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

}