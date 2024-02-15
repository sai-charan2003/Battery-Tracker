package com.example.battery_tracker.Screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battery_tracker.viewmodel
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun uiscreen() {
    val application = LocalContext.current.applicationContext
    val viewmodel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(application = application) as T
            }
        }
    )


    val context= LocalContext.current
    val sharedpreferences: SharedPreferences = context.getSharedPreferences("Devicename",Context.MODE_PRIVATE)
    val devicename by remember {
        mutableStateOf(sharedpreferences.getString("Devicename", Build.MODEL))
    }
    LaunchedEffect(key1 = Unit) {
        if (isWearDeviceConnected(context)) {
//            Log.d("TAG", "uiscreen: connected")
        } else {
//            Log.d("TAG", "uiscreen: not connected")
        }

    }

    viewmodel.batterydata()
    val color by remember {
        mutableStateOf(Color.Green)
    }
    val lowpowercolor by remember {
        mutableStateOf(Color.Yellow)
    }
    val coroutine = rememberCoroutineScope()
    Scaffold() {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)

        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${viewmodel.batterylevel}",
                        fontWeight = FontWeight.W900,
                        fontSize = 50.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp,
                            bottom = 8.dp,

                            )
                    )
                    Text(
                        text = "%",
                        fontWeight = FontWeight.W700,
                        fontSize = 20.sp,

                        modifier = Modifier.padding(
                            top = 35.dp,

                            )
                    )
                }
                if(viewmodel.islowpower){
                    LinearProgressIndicator(
                        progress = { viewmodel.batterylevel / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                5.dp
                            )
                            .size(10.dp)
                            .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)),
                        color = Color.Yellow,
                    )
                }
                else{
                    LinearProgressIndicator(
                        progress = { viewmodel.batterylevel / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                5.dp
                            )
                            .size(10.dp)
                            .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)),
                        color = Color.Green,
                    )
                }
                Text(
                    text = "Remaining Capacity",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = "${viewmodel.remainingcapacity / 1000}mAh",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Battery Status",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = viewmodel.ischargingstatus,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Battery Type",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = viewmodel.batterytype,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Health Info",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = viewmodel.healthstate,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Temperature",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = "${viewmodel.tempInCelsius}°C",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Voltage",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = "${viewmodel.voltage / 1000f}V",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )


                if (viewmodel.ischargingstatus == "Charging") {

                    if (viewmodel.chargingtype != "USB") {
                        Text(
                            text = "Charge Time Remaining",
                            modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                            fontSize = 17.sp
                        )
                        Text(
                            text = "${viewmodel.chargecompute}Minutes",
                            modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                        )
                    }
                    Text(
                        text = "Charging Type",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                        fontSize = 17.sp
                    )
                    Text(
                        text = viewmodel.chargingtype,
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                }
            }
        }
    }

}
fun isWearDeviceConnected(context: Context): Boolean {
    val nodeClient = Wearable.getNodeClient(context)

    try {
        // Fetch the connected nodes
        val nodes: Collection<Node> = Tasks.await(nodeClient.connectedNodes)

        // Check if there is at least one connected node (Wear OS device)
        return nodes.isNotEmpty()
    } catch (exception: Exception) {
        // Handle the exception (e.g., GoogleApiAvailabilityException)
        return false
    }
}
fun getNodes(context: Context): Collection<String> {

    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.displayName }
}
fun getnodenames(context:Context):Collection<String>{
    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.displayName }
}
