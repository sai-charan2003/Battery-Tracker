package dev.charan.batteryTracker.presentation.home.components

import android.widget.Space
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailsInfoRow(
    title : String,
    body : String,
    modifier : Modifier = Modifier
) {
    TitleText(text = title)
    Spacer(Modifier.height(10.dp))
    BodyText(text = body)
    Spacer(modifier = Modifier.height(20.dp))

}

@Composable
fun TitleText(
    text:String,
    modifier: Modifier = Modifier){
    Text(
        text = text,
        modifier = Modifier.then(modifier),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

}

@Composable
fun BodyText(text: String,modifier: Modifier = Modifier){
    Text(
        text = text,
        modifier = Modifier.then(modifier),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
    )
}