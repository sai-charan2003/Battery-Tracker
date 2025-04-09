package dev.charan.batteryTracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.presentation.home.HomeScreen
import dev.charan.batteryTracker.presentation.theme.BatteryTrackerTheme
import dev.charan.batteryTracker.utils.convertToBatteryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            var sendMessage by remember { mutableStateOf(true) }
            var batteryInfo by remember { mutableStateOf(BatteryInfo()) }
            var phoneData by remember { mutableStateOf("") }
            Wearable.getMessageClient(this).addListener {
                phoneData=String(it.data)
                Log.d("TAG", "Greeting: $phoneData")
            }
            LaunchedEffect(phoneData) {
                Log.d("TAG", "onCreate: $phoneData")
                batteryInfo = phoneData.convertToBatteryModel()

            }
            LaunchedEffect(sendMessage) {
                launch (Dispatchers.IO) {
                    getNodes(this@MainActivity).forEach { nodeId ->
                        Wearable.getMessageClient(this@MainActivity).sendMessage(
                            nodeId,
                            "/deploy",
                            MESSAGE_TEXT.toByteArray()
                        ).apply {
                            addOnSuccessListener { Log.d("TAG", "Message sent to $nodeId") }
                            addOnFailureListener { Log.d("TAG", "Failed to send message to $nodeId : $it") }
                            sendMessage = false
                        }


                    }.toString()
                }
            }
            BatteryTrackerTheme {
                HomeScreen(
                    batteryInfo,
                    fetchBattery = {
                        sendMessage = true
                    }
                )
            }

        }
    }
    companion object {
        const val MESSAGE_TEXT = "getbattery"
    }
}
private fun getNodes(context: Context): Collection<String> {
    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.id }
}
