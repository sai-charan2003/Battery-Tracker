package dev.charan.batteryTracker.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePhoneNameBottomSheet(
    focusRequester: FocusRequester,
    currentName: String,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            OutlinedTextField(
                value = currentName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                label = { Text("Change Name") }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(onClick = onSubmit) { Text("Change") }
            }
        }
    }
}