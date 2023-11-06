//The App design is inspired from aBattery - Battery health by san_laam
//Link for the aBattery app: https://play.google.com/store/apps/details?id=me.linshen.abattery


package com.example.battery_widget

import android.Manifest
import android.annotation.SuppressLint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battery_widget.ui.theme.Battery_widgetTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Battery_widgetTheme(darkTheme = isSystemInDarkTheme()) {
                           Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                               val applicationcontext=applicationContext
                    if (Build.VERSION.SDK_INT >= 31) {

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
                            uiscreen(applicationcontext)

                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun uiscreen(application: Context){
    val viewmodel= viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(application = application) as T
            }
        }
    )

    viewmodel.batterydata()
            val firebaseDatabase=FirebaseDatabase.getInstance()
    var version by remember {
        mutableStateOf("0")
    }
    var downloadlink by remember {
        mutableStateOf("null")
    }
    var newversion by remember {
        mutableStateOf(false)
    }
    var list = remember {
        mutableListOf<Any>()
    }
    var newfeatures = remember {
        mutableListOf<Any>()
    }
    var link by remember {
        mutableStateOf("null")
    }
    val reference=firebaseDatabase.reference

    reference.addValueEventListener(object :ValueEventListener{

        override fun onDataChange(snapshot: DataSnapshot) {


            if(snapshot.exists()){
                Log.d("TAG", "onDataChange: ${snapshot.value}")
                for(data in snapshot.children){
                    Log.d("TAG", "onDataChange: ${data.value}")
                    data.value?.let { list.add(it) }

                }
                Log.d("newvalues", "onDataChange: $list")
                link= list[1].toString()
                version= list[2].toString()
                downloadlink=list[0].toString()

                val appversion= "3.0"
                if(appversion!=version){
                    newversion=true

                }


            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
            var showmenu by remember {
                mutableStateOf(false
                )
            }
            var AlertDialog by remember {
                mutableStateOf( false)
            }
             var showbottomsheet by remember {
        mutableStateOf( false)
    }
            val Scroll= TopAppBarDefaults.pinnedScrollBehavior()
            Scaffold(
                modifier= Modifier
                    .fillMaxSize()
                    .nestedScroll(Scroll.nestedScrollConnection),

                topBar = {
                    TopAppBar(
                        title = { Text(text = "Battery") },
                        scrollBehavior = Scroll,
                        actions = {
                            IconButton(onClick = { showmenu = !showmenu }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                            DropdownMenu(
                                expanded = showmenu,
                                onDismissRequest = { showmenu = false }) {
                                DropdownMenuItem(
                                    text = { Text(text = "Check for Updates") },
                                    onClick = { AlertDialog = true
                                        showmenu=false

                                    })
                                DropdownMenuItem(
                                    text = { Text(text = "What's New") },
                                    onClick = { showbottomsheet = true
                                        showmenu=false

                                    })
                            }
                        },


                        )

                }


            ) { values->

                if(AlertDialog){
                    ModalBottomSheet(onDismissRequest = { AlertDialog=false }) {
                        Column(modifier=Modifier.padding(start = 10.dp, end = 5.dp)) {

                            if(newversion){
                                Text(text = "New Version Available", style = MaterialTheme.typography.headlineSmall, modifier= Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),textAlign = TextAlign.Center, color = Color.Red)
                            }
                            else{
                                Text(text = "You are up to date", style = MaterialTheme.typography.headlineSmall, modifier= Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),textAlign = TextAlign.Center)
                            }
                            val context = LocalContext.current
                            val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("$link")) }
                            Text(text = "Version: $version",modifier=Modifier.padding(bottom = 8.dp))
                            Row() {
                                Button(onClick = { context.startActivity(intent) },modifier=Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                                    Text(text = "Project On Github")

                                }
//                                Text(text = "Source Code: ")
//                                Text(
//                                    text = "$link",
//                                    modifier = Modifier.clickable { context.startActivity(intent) },
//                                    color = Color(0xFF2f81f7)
//                                )
                            }

                            if(newversion) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Bottom
                                ) {


                                    val context = LocalContext.current
                                    val intent = remember {
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("$downloadlink")
                                        )
                                    }

                                    Button(onClick = {
                                        context.startActivity(intent)
                                    }, modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)) {
                                        Text(text = "Update")

                                    }
                                }
                            }

                        }

                    }
                }


                if(showbottomsheet){
                    ModalBottomSheet(onDismissRequest = { showbottomsheet=false }) {
                        Column(modifier=Modifier.padding(start = 10.dp, bottom = 10.dp  )) {


                            Text(
                                text = "📲 Added new what's new page",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "📨 Added Broadcast Receiver. Now battery details will be updated automatically.",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "🔃 Removed Pull to refresh",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "❤️ New Icon Background",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )


                            Text(
                                text = "🐛 Fixed Some Bugs in app",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(text = "🪲 Fixed Scaling Issue in Widget")
                        }
                        
                    }
                }


                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(values)){
                    item {
                        Row(modifier=Modifier.fillMaxWidth()) {
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
                        if(viewmodel.batterylevel>=40) {
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
                        else if(viewmodel.batterylevel<40) {
                            LinearProgressIndicator(
                                progress = { viewmodel.batterylevel / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        5.dp
                                    )
                                    .size(10.dp)
                                    .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)),
                                color = Color(0xff61F767),
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
                                color = Color.Red,
                            )
                            }
                        Text(text = "Remaining Capacity", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.remainingcapacity/1000}mAh",modifier = Modifier.padding(top=3.dp, start = 10.dp))
                        Text(text = "Battery Status", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.ischargingstatus}",modifier = Modifier.padding(top=3.dp, start = 10.dp))
                        Text(text = "Battery Type", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.batterytype}",modifier = Modifier.padding(top=3.dp, start = 10.dp))
                        Text(text = "Health Info", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.healthstate}",modifier = Modifier.padding(top=3.dp, start = 10.dp))
                        Text(text = "Temperature", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.tempInCelsius}°C",modifier = Modifier.padding(top=3.dp, start = 10.dp))
                        Text(text = "Voltage", modifier = Modifier.padding(top=30.dp, start = 10.dp), fontSize = 17.sp)
                        Text(text = "${viewmodel.voltage/1000f}V",modifier = Modifier.padding(top=3.dp, start = 10.dp))

                        if(viewmodel.ischargingstatus=="Charging"){

                            if(viewmodel.chargingtype!="USB") {
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
                                    text = "${viewmodel.chargingtype}",
                                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                                )
                        }
                    }
                }
            }
        }

//class batterybroadcast(viewmodel: viewmodel,intent: Intent?,context: Context?): BroadcastReceiver() {
//    val viewmodel=viewmodel
//    val codeintent=intent
//
//    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//        viewmodel.batterydata()
//
//
//        val status: Int = intent!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//        viewmodel.chargingstatus=intent!!.getIntExtra(BatteryManager.EXTRA_PLUGGED,0)
//        viewmodel.batterystatus=intent.getIntExtra(BatteryManager.EXTRA_STATUS,0)
//        viewmodel.ischargingstatus= getchargingstatus(viewmodel.batterystatus)
//
//        viewmodel.chargingtype= getplugged(viewmodel.chargingstatus)
//        viewmodel.chargecompute=(batteryManager.computeChargeTimeRemaining()/60000f
//                ).toString().substringBefore(".")
//    }
//
//}

















