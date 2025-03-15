package dev.charan.batteryTracker.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CheckForUpdateDialog(
    isFetchingData: Boolean,
    latestVersion: String?,
    onDismiss: () -> Unit,
    onUpdateClick: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        confirmButton = {
            if (!isFetchingData && latestVersion != null) {
                TextButton(onClick = onUpdateClick) { Text("Update") }
            }
        },
        title = {
            if (!isFetchingData) {
                Text(text = latestVersion?.let { "New Version Available" } ?: "You are up to date", color = Color.Red)
            }
        },
        text = {
            if (isFetchingData) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    latestVersion?.let { Text("Version: $it") }
                }
            }
        }
    )
}