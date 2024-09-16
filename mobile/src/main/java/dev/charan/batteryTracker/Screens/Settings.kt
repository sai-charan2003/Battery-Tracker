package dev.charan.batteryTracker.Screens


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.charan.batteryTracker.Screens.components.SettingsItem
import dev.charan.batteryTracker.Utils.PermissionCheck
import dev.charan.batteryTracker.Utils.SharedPref
import dev.charan.batteryTracker.data.model.AutoUpdateDTO
import dev.charan.batteryTracker.data.model.ProcessState
import dev.charan.batteryTracker.viewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun Settings(navHostController: NavHostController) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var changenamebottomsheet by remember {
        mutableStateOf(false)
    }

    val application = LocalContext.current.applicationContext
    val haptic = LocalHapticFeedback.current
    val sharedPref = SharedPref.getInstance(application)
    var sliderPosition by remember { mutableStateOf(sharedPref.minWearosBattery!!.toFloat()) }
    var sliderPositionForHeadphones by remember { mutableStateOf(sharedPref.minHeadphonesBattery!!.toFloat()) }
    var latestAppVersion by remember {
        mutableStateOf<AutoUpdateDTO?>(null)
    }
    var isFetchingTheData by remember {
        mutableStateOf(true)
    }


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


    sharedPref.isNotificationAllowed = if (PermissionCheck.notificationPermissionEnabled(context)) {
        sharedPref.isNotificationAllowed
    } else {
        false
    }
    var isNotificationAllowed by remember {
        mutableStateOf(sharedPref.isNotificationAllowed)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var whatsnewbottomsheet by remember {
        mutableStateOf(false)
    }

    var showCheckForUpdate by remember {
        mutableStateOf(false)
    }

    var changeMinimumBatteryLevel by remember {
        mutableStateOf(false)
    }
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isNearByDevicesPermissionEnabled by remember {
        mutableStateOf(PermissionCheck.nearbyDevicePermissionEnabled(context))
    }
    LaunchedEffect(Unit) {
        viewmodel.getLatestAppVersionFromAPI()?.observeForever {
            when(it){
                is ProcessState.Loading -> {
                    isFetchingTheData = true
                }
                is ProcessState.Success -> {
                    latestAppVersion = it.autoUpdateDTO
                    isFetchingTheData = false
                }
                else -> {
                    isFetchingTheData = false
                }
            }
        }
    }


    val intent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/sai-charan2003/Battery-Tracker")
        )
    }


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
                SettingsItem(title = "Change Phone Name") {
                    changenamebottomsheet = true
                }
                HorizontalDivider()

                SettingsItem(title = "Notification Settings") {
                    changeMinimumBatteryLevel = true

                }
                HorizontalDivider()
                SettingsItem(title = "Project On Github") {
                    context.startActivity(intent)

                }
                HorizontalDivider()
                SettingsItem(title = "Check for update") {
                    showCheckForUpdate = true
                }
            }

        }

        fun requestPermission() {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        fun openAppSettings() {

            val intent = Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun checkForPermission() {

            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isNotificationAllowed = true
                sharedPref.isNotificationAllowed = true
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    openAppSettings()
                } else {
                    requestPermission()
                }

            }

        }

        fun requestNearbyDevicePermission() {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                2
            )
        }

        fun checkForBluetoothPermission() {

            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isNearByDevicesPermissionEnabled = true


            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    )
                ) {
                    requestNearbyDevicePermission()
                } else {
                    openAppSettings()
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
                            if (changedevicename != "") {
                                sharedPref.deviceName = changedevicename
                                changenamebottomsheet = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Click on the widget to update name")
                                }

                            }
                        }) {
                            Text("Change")


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
        if (changeMinimumBatteryLevel) {
            ModalBottomSheet(onDismissRequest = { changeMinimumBatteryLevel = false }) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Notifications", modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                        Switch(checked = isNotificationAllowed, onCheckedChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (it) {
                                checkForPermission()
                            } else {
                                sharedPref.isNotificationAllowed = false
                                isNotificationAllowed = false
                            }
                        })

                    }
                    if (isNearByDevicesPermissionEnabled) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Wear OS Low Battery Alert", modifier = Modifier.weight(1f))
                            Text(text = sharedPref.minWearosBattery?.toFloat()?.toInt().toString())

                        }


                        Slider(
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            },
                            value = sliderPosition,
                            onValueChange = {
                                val value = it.roundToInt()
                                sliderPosition = value.toFloat()
                                sharedPref.minWearosBattery = it.toString()
                            },
                            valueRange = 0f..90f,
                            onValueChangeFinished = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)


                            },

                            steps = 8
                        )
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Headphones Low Battery Alert",
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = sharedPref.minHeadphonesBattery?.toInt().toString())

                        }


                        Slider(
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            },
                            value = sliderPositionForHeadphones,
                            onValueChange = {
                                val value = it.roundToInt()

                                sliderPositionForHeadphones = value.toFloat()
                                sharedPref.minHeadphonesBattery = value.toString()
                            },
                            valueRange = 0f..90f,
                            onValueChangeFinished = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },

                            steps = 8
                        )
                    } else {
                        Row(modifier = Modifier
                            .clickable { checkForBluetoothPermission() }
                            .padding(10.dp)) {
                            Text(
                                text = "Enable Bluetooth Permission", modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            )


                        }
                    }

                }


            }
        }
        if(showCheckForUpdate){
            LaunchedEffect(Unit) {
                viewmodel.getLatestAppVersionFromAPI()?.observeForever {
                    when(it){
                        is ProcessState.Loading -> {
                            isFetchingTheData = true
                        }
                        is ProcessState.Success -> {
                            latestAppVersion = it.autoUpdateDTO
                            isFetchingTheData = false
                        }
                        else -> {
                            isFetchingTheData = false
                        }
                    }
                }
            }

            val newversion = if(latestAppVersion?.appVersion.toString() != dev.charan.batteryTracker.BuildConfig.VERSION_NAME) true else false
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { showCheckForUpdate=false },
                confirmButton = {
                    val context = LocalContext.current
                    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("${latestAppVersion?.appDownloadLink}")) }

                    if(newversion && !isFetchingTheData){
                        TextButton(onClick = {
                            context.startActivity(intent)
                        }) {
                            Text(text="Update")

                        }
                    }
                },
                title = {
                    if(!isFetchingTheData){
                        if (latestAppVersion != null) {
                            if (newversion) {
                                Text(
                                    text = "New Version Available",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(text = "You are up to date")
                            }
                        } else {
                            Text("Error Occurred Please Try Again")
                        }
                    }

                },
                text = {
                    if(!isFetchingTheData) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Version: ${latestAppVersion?.appVersion}",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )


                        }
                    } else{
                        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()

                        }

                    }

                },




                )
        }
    }
}









