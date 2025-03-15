package dev.charan.batteryTracker.presentation.settings


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import dev.charan.batteryTracker.presentation.settings.components.SettingsItem

import dev.charan.batteryTracker.Utils.PermissionCheck
import dev.charan.batteryTracker.presentation.settings.components.ChangePhoneNameBottomSheet
import dev.charan.batteryTracker.presentation.settings.components.CheckForUpdateDialog
import dev.charan.batteryTracker.presentation.settings.components.DarkModeToggle
import dev.charan.versiontracker.VersionTracker
import dev.charan.versiontracker.model.ProcessState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.google.accompanist.permissions.shouldShowRationale
import dev.charan.batteryTracker.presentation.settings.components.NotificationSettingsBottomSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Settings(
    navHostController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showChangePhoneNameBottomSheet by remember { mutableStateOf(false) }
    var showNotificationSettingsBottomSheet by remember { mutableStateOf(false) }
    var showCheckForUpdateDialog by remember { mutableStateOf(false) }

    val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val bluetoothPermissionState = rememberPermissionState(Manifest.permission.BLUETOOTH_CONNECT)
    LaunchedEffect(notificationPermissionState.status) {
        when(notificationPermissionState.status){
            is PermissionStatus.Denied -> {

            }
            PermissionStatus.Granted -> {
                viewModel.onEvent(SettingsEvent.onNotificationPermissionGrant)

            }
        }
    }
    LaunchedEffect(bluetoothPermissionState.status) {
        when(notificationPermissionState.status){
            is PermissionStatus.Denied -> {

            }
            PermissionStatus.Granted -> {
                viewModel.onEvent(SettingsEvent.onBluetoothPermissionGrant)

            }
        }
    }
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest {
            when(it){
                is SettingsEffect.RequestNertByPermission -> {
                    bluetoothPermissionState.launchPermissionRequest()

                }
                is SettingsEffect.RequestNotificationPermission -> {
                    notificationPermissionState.launchPermissionRequest()

                }

                is SettingsEffect.OpenGithub -> {
                    val intent = Intent(Intent.ACTION_VIEW, it.url.toUri())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                }
                SettingsEffect.OpenSettings -> {
                    val intent = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scroll
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                SettingsItem(title = "Change Phone Name") { showChangePhoneNameBottomSheet = true }
                HorizontalDivider()
                SettingsItem(title = "Notification Settings") { showNotificationSettingsBottomSheet = true }
                HorizontalDivider()
                SettingsItem(title = "Project On Github") { viewModel.onEvent(SettingsEvent.onGithubOpen) }
                HorizontalDivider()
                SettingsItem(title = "Check for update") { showCheckForUpdateDialog = true }
//                HorizontalDivider()
//                DarkModeToggle(state.isDarkModeEnabled) { viewModel.onEvent(SettingsEvent.onChangeDarkMode) }
            }
        }

        if (showChangePhoneNameBottomSheet) {
            ChangePhoneNameBottomSheet(
                focusRequester = focusRequester,
                currentName = state.phoneName,
                onDismiss = { showChangePhoneNameBottomSheet = false },
                onNameChange = { viewModel.onEvent(SettingsEvent.onChangePhoneName(it)) },
                onSubmit = {
                    viewModel.onEvent(SettingsEvent.onChangePhonenNameSubmit)
                    showChangePhoneNameBottomSheet = false
                }
            )
        }

        if (showCheckForUpdateDialog) {
            CheckForUpdateDialog(
                isFetchingData = false,
                latestVersion = "1.0.1",
                onDismiss = { showCheckForUpdateDialog = false },
                onUpdateClick = {  }
            )
        }
        if (showNotificationSettingsBottomSheet) {
            NotificationSettingsBottomSheet(
                state = state,
                onDismiss = { showNotificationSettingsBottomSheet = false },
                onToggleNotification = { viewModel.onEvent(SettingsEvent.onNotificationPermissionChange(notificationPermissionState.status.shouldShowRationale)) },
                onSliderValueChange = { viewModel.onEvent(SettingsEvent.onChangeWearOsBatteryLevel(it)) },
                onHeadphonesSliderChange = { viewModel.onEvent(SettingsEvent.onChangeHeadPhonesBatteryLevel(it)) },
                onRequestBluetoothPermission = { viewModel.onEvent(SettingsEvent.onBluetoothPermissionChange(bluetoothPermissionState.status.shouldShowRationale)) }
            )
        }
    }
}














