package com.example.battery_widget.Screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battery_widget.viewmodel

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

    viewmodel.batterydata()
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
                if (viewmodel.batterylevel >= 40) {
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
                } else if (viewmodel.batterylevel < 40) {
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
                } else {
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
                    text = "${viewmodel.ischargingstatus}",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Battery Type",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = "${viewmodel.batterytype}",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Health Info",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = "${viewmodel.healthstate}",
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
                        text = "${viewmodel.chargingtype}",
                        modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                    )
                }
            }
        }
    }
}