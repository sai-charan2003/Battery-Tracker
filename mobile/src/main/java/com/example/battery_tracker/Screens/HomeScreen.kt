package com.example.battery_tracker.Screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.battery_tracker.Navigation.Destination
import com.example.battery_tracker.Screens.components.BodyText
import com.example.battery_tracker.Screens.components.TitleText
import com.example.battery_tracker.Utils.SharedPref
import com.example.battery_tracker.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val viewmodel = viewModel<viewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModel(application = application) as T
            }
        }
    )
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context= LocalContext.current
    val sharedPref = SharedPref.getInstance(context)

    val deviceName by remember {
        mutableStateOf(sharedPref.deviceName)
    }


    viewmodel.batteryData()
    val color by remember {
        mutableStateOf(Color.Green)
    }
    val lowpowercolor by remember {
        mutableStateOf(Color.Yellow)
    }
    var showdropdownmenu by remember {
        mutableStateOf(false)
    }
    val coroutine = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Battery Tracker") },
                scrollBehavior = scroll,
                actions = {
                    IconButton(onClick = { showdropdownmenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "more"
                        )
                    }
                    DropdownMenu(
                        expanded = showdropdownmenu,
                        onDismissRequest = { showdropdownmenu=false }) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { navHostController.navigate(Destination.settings.route) })

                        
                    }


                }

                )
        }
        
    ) {


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
                                10.dp
                            )
                            .size(10.dp)
                            ,
                        color = Color.Yellow,
                    )
                }
                else{
                    LinearProgressIndicator(
                        progress = { viewmodel.batterylevel / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                10.dp
                            )
                            .size(10.dp)
                            ,
                        color = Color.Green,
                    )
                }

                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    ),
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(10.dp)) {
                    TitleText(
                        text = "Remaining Capacity",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    )
                    BodyText(
                        text = "${viewmodel.remainingcapacity / 1000}mAh",
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                    TitleText(
                        text = "Battery Status",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                    )
                    BodyText(
                        text = viewmodel.ischargingstatus,
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                    TitleText(
                        text = "Battery Type",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                    )
                    BodyText(
                        text = viewmodel.batterytype,
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                    TitleText(
                        text = "Health Info",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                    )
                    BodyText(
                        text = viewmodel.healthstate,
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                    TitleText(
                        text = "Temperature",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                    )
                    BodyText(
                        text = "${viewmodel.tempInCelsius}°C",
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                    TitleText(
                        text = "Voltage",
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                    )
                    BodyText(
                        text = "${viewmodel.voltage / 1000f}V",
                        modifier = Modifier
                            .padding(top = 3.dp, start = 10.dp)
                            .then(
                                if(viewmodel.ischargingstatus!="Charging"){
                                    Modifier.padding(bottom = 30.dp)
                                } else{
                                    Modifier
                                }
                            )
                    )


                    if (viewmodel.ischargingstatus == "Charging") {

                        if (viewmodel.chargingtype != "USB") {
                            TitleText(
                                text = "Charge Time Remaining",
                                modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                            )
                            BodyText(
                                text = "${viewmodel.chargecompute}Minutes",
                                modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                            )
                        }
                        TitleText(
                            text = "Charging Type",
                            modifier = Modifier.padding(top = 30.dp, start = 10.dp),

                        )
                        BodyText(
                            text = viewmodel.chargingtype,
                            modifier = Modifier.padding(top = 3.dp, start = 10.dp,bottom=30.dp)
                        )
                    }

                }

            }
        }
    }

}

