package dev.charan.batteryTracker.presentation.settings

sealed interface SettingsEvent {
    data class onChangePhoneName(val name : String) : SettingsEvent
    object onChangePhonenNameSubmit : SettingsEvent
    data class onChangeHeadPhonesBatteryLevel(val level : Float) : SettingsEvent
    data class onChangeWearOsBatteryLevel(val level : Float) : SettingsEvent
    data object onChangeDarkMode : SettingsEvent
    object onGithubOpen : SettingsEvent
    object onCheckForUpdate : SettingsEvent
    data class onNotificationPermissionChange(val showRational : Boolean) : SettingsEvent
    data class onBluetoothPermissionChange(val showRational: Boolean) : SettingsEvent
    object onNotificationPermissionGrant : SettingsEvent
    object onBluetoothPermissionGrant : SettingsEvent

}