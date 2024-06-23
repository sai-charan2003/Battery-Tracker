package com.example.battery_tracker.Screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.navigation.NavHostController
import com.example.battery_tracker.Utils.SharedPref
import com.example.battery_tracker.viewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun Settings(navHostController: NavHostController) {




    val focusRequester = remember { FocusRequester() }
    var changenamebottomsheet by remember {
        mutableStateOf(false)
    }

    val application = LocalContext.current.applicationContext
    val haptic = LocalHapticFeedback.current
    val sharedPref = SharedPref.getInstance(application)
    var sliderPosition by remember { mutableStateOf(sharedPref.minWearosBattery!!.toFloat()) }
    val viewmodel = viewModel<viewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModel(application = application) as T
            }
        }
    )
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var changedevicename by remember {
        mutableStateOf("")
    }
    val snackbarscope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var checkforupdatebottomsheet by remember {
        mutableStateOf(false)
    }
    var whatsnewbottomsheet by remember {
        mutableStateOf(false)
    }
    val firebaseDatabase = FirebaseDatabase.getInstance()
    var version by remember {
        mutableStateOf("0")
    }
    var downloadlink by remember {
        mutableStateOf("null")
    }
    var newversion by remember {
        mutableStateOf(false)
    }
    var link by remember {
        mutableStateOf("null")
    }
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val reference = firebaseDatabase.reference
    var list = remember {
        mutableListOf<Any>()
    }
    reference.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {


            if (snapshot.exists()) {

                for (data in snapshot.children) {

                    Log.d("TAG", "onDataChange: ${data.value}")
                    data.value?.let { list.add(it) }

                }

                link = list[1].toString()
                version = list[2].toString()
                downloadlink = list[0].toString()

                val appversion = "4.1"
                if (appversion != version) {
                    newversion = true

                }


            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Arrow",

                        )
                    }
                },
                scrollBehavior = scroll
                )
        }

        ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                ListItem( {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Change phone name on widget")


                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = "Arrow")
                    }


                },
                    modifier=Modifier.clickable { changenamebottomsheet=true }
                )
                HorizontalDivider()

                ListItem( {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Check for update")

                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = "Arrow")
                    }


                },
                    modifier=Modifier.clickable { checkforupdatebottomsheet=true }
                )
                HorizontalDivider()
                ListItem( {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "What's new")

                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = "Arrow")
                    }


                },
                    modifier=Modifier.clickable { whatsnewbottomsheet=true }
                )
                HorizontalDivider()
                ListItem( {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),

                    ) {
                        Row {
                            Text(text = "Wear OS Low Battery Alerts",modifier=Modifier.weight(1f))
                            if(sliderPosition==0f){
                                Text(text = "Off")
                            }
                            else{
                                Text(sliderPosition.toInt().toString())
                            }



                        }


                        Slider(
                            modifier = Modifier.semantics { contentDescription = "Localized Description" },
                            value = sliderPosition,
                            onValueChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                sliderPosition = it
                                sharedPref.minWearosBattery=it.toString()
                                            },
                            valueRange = 0f..90f,
                            onValueChangeFinished = {


                            },

                            steps = 8
                        )
                    }


                },

                )
                HorizontalDivider()

                val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sai-charan2003/Battery-Tracker")) }
                val context = LocalContext.current
                ListItem( {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Project on Github")

                        Icon(
                            imageVector = Icons.Filled.ArrowRight,
                            contentDescription = "Arrow"
                        )
                    }


                },
                    modifier=Modifier.clickable { context.startActivity(intent) }
                )


            }



        }

    }
    if (changenamebottomsheet) {

        ModalBottomSheet(
            onDismissRequest = { changenamebottomsheet = false },
            sheetState = sheetState
        ) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = changedevicename,
                    onValueChange = {
                        changedevicename = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = { Text("Change Name") },
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalAlignment = Alignment.End,
                ) {

                    FilledTonalButton(onClick = {
                        if(changedevicename!=""){
                            sharedPref.deviceName=changedevicename
                            changenamebottomsheet=false
                            scope.launch {
                                snackbarHostState.showSnackbar("Click on the widget to update name")
                            }

                        }}) {
                        Text("Change")


                    }
                }


            }


        }

    }
    if (checkforupdatebottomsheet) {
        ModalBottomSheet(onDismissRequest = { checkforupdatebottomsheet = false }) {
            Column(modifier = Modifier.padding(start = 10.dp, end = 5.dp)) {

                if (newversion) {
                    Text(
                        text = "New Version Available",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth(),

                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                } else {
                    Text(
                        text = "You are up to date",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                val context = LocalContext.current

                Text(text = "Version: $version", modifier = Modifier.padding(top=10.dp,bottom=10.dp))


                if (newversion) {
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
                        }, modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth()) {
                            Text(text = "Update")

                        }
                    }
                }

            }

        }
    }
    if (whatsnewbottomsheet) {
        ModalBottomSheet(onDismissRequest = { whatsnewbottomsheet = false }) {
            Column(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)) {
                Text(
                    text = "📱 Now battery progress indicator will turn yellow when low power mode is on",
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color.Green
                )

                Text(
                    text = "📱 New UI with bottom navigation bar",
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "📲 Added new what's new page",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "📨 Added Broadcast Receiver. Now battery details will be updated automatically.",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "ℹ️ Added new device Information page",
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "📝 Now you can edit phone name on widget",
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
                Text(text = "🪲 Fixed Scaling Issue in Widget")
            }

        }
    }
}


