/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package dev.charan.batteryTracker.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton

import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import dev.charan.batteryTracker.R
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.charan.batteryTracker.presentation.theme.Battery_TrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val context= LocalContext.current
            LaunchedEffect(key1 = Unit) {

                val text="getbattery"
                launch (Dispatchers.IO) {
                    Log.d("TAG", "onCreate: ${getNodenames(context)}")

                    getNodes(context).forEach { nodeId ->
                        Wearable.getMessageClient(context).sendMessage(
                            nodeId,
                            "/deploy",
                            text.toByteArray()
                        ).apply {
                            addOnSuccessListener { Log.d("TAG", "OnSuccess") }
                            addOnFailureListener { Log.d("TAG", "OnFailure") }
                        }


                    }.toString()
                    Log.d("TAG", "onCreate: ${getNodes(context)}")
                }




            }
            Wearable.getMessageClient(context).addListener {

                Log.d("TAG", "Greeting: ${String(it.data)}")

            }
            WearApp("Android")

        }
    }
    fun updateGreetingName(name: String) {
        val data=name
    }
}

@Composable
fun WearApp(greetingName: String) {
    Battery_TrackerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    val coroutineScope= rememberCoroutineScope()

    val context= LocalContext.current

    var data by remember {
        mutableStateOf("")
    }
    var devicename by remember {
        mutableStateOf("")
    }
    coroutineScope.launch(Dispatchers.IO) {
        getNodenames(context).forEach {
            devicename=it
        }

    }
    var ischarging by remember {
        mutableStateOf("false")
    }

    Wearable.getMessageClient(context).addListener {
        data=String(it.data)
        Log.d("TAG", "Greeting: $data")
        ischarging=data.substringAfter("ischarging")
    }
    if(data!="") {
        if(ischarging=="false") {
            CircularProgressIndicator(
                progress = data.substringBefore("ischarging").toFloat() / 100f,
                modifier = Modifier.fillMaxSize(),
                startAngle = 290f,
                endAngle = 250f,
                strokeWidth = 4.dp
            )
        }
        else{
            CircularProgressIndicator(
                progress = data.substringBefore("ischarging").toFloat() / 100f,
                modifier = Modifier.fillMaxSize(),
                startAngle = 290f,
                endAngle = 250f,
                strokeWidth = 4.dp,
                indicatorColor = Color.Green

            )

        }

        Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            Text(text= devicename, textAlign = TextAlign.Center,modifier=Modifier.padding(bottom=10.dp))




            Row(verticalAlignment = Alignment.CenterVertically,modifier=Modifier.padding(bottom=10.dp)) {
                if(data.substringAfter("ischarging")=="true"){
                    Icon(painter = painterResource(id = R.drawable.charging), contentDescription = null)
                }
                Text(
                    text = "${data.substringBefore("ischarging")}%",
                    textAlign = TextAlign.Center,

                    style = MaterialTheme.typography.title1

                )

            }
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch (Dispatchers.IO){

                            val text = "getbattery"


                            getNodes(context).forEach { nodeId ->
                                Wearable.getMessageClient(context).sendMessage(
                                    nodeId,
                                    "/deploy",
                                    text.toByteArray()
                                ).apply {
                                    addOnSuccessListener { Log.d("TAG", "OnSuccess") }
                                    addOnFailureListener { Log.d("TAG", "OnFailure") }
                                }


                            }.toString()
                            Log.d("TAG", "onCreate: ${getNodes(context)}")
                        }
                    },
                    modifier = Modifier
                        .size(ButtonDefaults.ExtraSmallButtonSize)
                        .padding(bottom = 10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)


                }
            }

    }
    else{
        Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            CircularProgressIndicator()

        }
    }




}


class PhoneListenerService: WearableListenerService() {

    val scope = CoroutineScope(Dispatchers.IO)
    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        Log.d("TAG", "onMessageReceived: ${p0.data}")
    }



    override fun onCreate() {
        val batteryIntent =
            applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
        val ischarging= ischargingfun(chargingstatus).toString()

        val batteryManager=getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        super.onCreate()
        val battery= getBatteryPercentage(applicationContext)
        val stringbattery=battery.toString()
        val devicename=android.os.Build.DEVICE
        val ouput=stringbattery+"ischarging"+ischarging

        scope.launch(Dispatchers.IO) {

            getNodes(applicationContext).forEach { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    "/deploy",
                    ouput.toByteArray()
                ).apply {
                    addOnSuccessListener { Log.d("TAG", "OnSuccess") }
                    addOnFailureListener { Log.d("TAG", "OnFailure") }
                }


            }.toString()

        }

    }



    companion object{
        private const val TAG = "PhoneListenerService"
        private const val MESSAGE_PATH = "/deploy"
    }
}
private fun getNodes(context: Context): Collection<String> {

    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.id }
}
private fun getNodenames(context: Context): Collection<String> {

    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.displayName }
}
private fun getBatteryPercentage(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }


    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

    return if (level == -1 || scale == -1) {
        0
    } else {
        // Calculate the battery percentage
        (level.toFloat() / scale.toFloat() * 100).toInt()
    }
}
fun ischargingfun(charging:Int):Boolean{

    var ischarging=false
    when(charging){
        BatteryManager.BATTERY_STATUS_CHARGING->ischarging=true

    }
    return ischarging

}