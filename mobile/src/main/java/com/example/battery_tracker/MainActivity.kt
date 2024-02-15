package com.example.battery_tracker

import Battery_Trackertheme
import android.Manifest
import android.annotation.SuppressLint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.battery_tracker.Navigation.NavigationApphost
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Battery_Trackertheme() {


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val applicationcontext = applicationContext
                    var selectedItem by remember { mutableIntStateOf(0) }
                    val items = listOf("Battery", "Device Info", "Settings")
                    val navController = rememberNavController()
                    val icons = listOf(
                        Icons.Outlined.BatteryFull,
                        Icons.Outlined.Info,
                        Icons.Outlined.Settings
                    )
                    val selectedicons =
                        listOf(Icons.Filled.BatteryFull, Icons.Filled.Info, Icons.Filled.Settings)
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Wearable.getMessageClient(this).addListener {
                        Log.d("TAG", "onCreate: ${it.data}")
                    }

                    if (ActivityCompat.checkSelfPermission(
                            LocalContext.current,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            1
                        )

                    }
                    val batterIntent = registerReceiver(
                        null,
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    )

                    if (batterIntent != null) {
                        val Scroll = TopAppBarDefaults.pinnedScrollBehavior()
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(Scroll.nestedScrollConnection),
                            topBar = {
                                TopAppBar(title = {
                                    if (selectedItem == 0) {
                                        Text(text = "Battery")
                                    } else if (selectedItem == 1) {
                                        Text(text = "Device Info")
                                    } else {
                                        Text("Settings")
                                    }
                                }, scrollBehavior = Scroll)
                            },


                            bottomBar = {
                                NavigationBar(content = {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(

                                            selected = selectedItem == index,
                                            onClick = {
                                                selectedItem =
                                                    index;


                                                navController.navigate(item) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },

                                            icon = {
                                                Icon(
                                                    imageVector = if (index == selectedItem) {
                                                        selectedicons[index]
                                                    } else {
                                                        icons[index]

                                                    },
                                                    contentDescription = items[index]
                                                )

                                            },
                                            label = { Text(text = item) }

                                        )


                                    }
                                })
                            }


                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    top = it.calculateTopPadding() - 40.dp,
                                    bottom = it.calculateBottomPadding()
                                )
                            ) {


                                NavigationApphost(navController = navController)

                            }
                        }

                    }
                }

            }
        }
    }
}
class PhoneListenerService: WearableListenerService() {




    val scope = CoroutineScope(Dispatchers.IO)
    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(TAG, String(messageEvent.data))

    }


    override fun onCreate() {
        Log.d(TAG, "onMessageReceived: hi from the lis")



        super.onCreate()
        val battery= getBatteryPercentage(applicationContext)
        val stringbattery=battery.toString()
        val devicename=Build.DEVICE


        val batteryIntent =
            applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val chargingstatus=batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS,0)
        val ischarging= ischargingfun(chargingstatus).toString()
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
private fun ischarging(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    var batterycharging = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, 0)


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
