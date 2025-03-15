package dev.charan.batteryTracker.presentation.settings

data class SettingsState(
    val isDarkModeEnabled: Boolean = false,
    val headPhonesMinimumBattery : Float = 0f,
    val wearOsMinimumBattery : Float = 0f,
    val phoneName : String = "",
    val isUpdateAvailable : Boolean = false,
    val latestVersion : String = "",
    val currentAppVersion : String = "",
    val isNotificationPermissionGranted : Boolean = false,
    val isNearByPermissionGranted : Boolean = false

)

