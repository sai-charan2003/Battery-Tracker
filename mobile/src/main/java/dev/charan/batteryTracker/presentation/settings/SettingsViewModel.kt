package dev.charan.batteryTracker.presentation.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.charan.batteryTracker.data.prefs.SharedPref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import dev.charan.batteryTracker.data.repository.WidgetRepository
import dev.charan.batteryTracker.utils.AppConstants
import dev.charan.batteryTracker.utils.SettingsUtils
import dev.charan.batteryTracker.widgets.Material3widget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val settingsUtils: SettingsUtils,
    private val widgetRepository: WidgetRepository
): ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()
    init {
        setInitialValues()
    }

    fun onEvent(settingsEvent: SettingsEvent){
        when(settingsEvent){
            is SettingsEvent.onChangeDarkMode -> {
                changeDarkMode()

            }
            is SettingsEvent.onChangeHeadPhonesBatteryLevel ->{
                changeMinHeadphoneBatteryLevel(settingsEvent.level)

            }
            is SettingsEvent.onChangePhoneName -> {
                updatePhoneName(settingsEvent.name)

            }
            is SettingsEvent.onChangeWearOsBatteryLevel -> {
                changeMinWearOsBatteryLevel(settingsEvent.level)

            }
            SettingsEvent.onCheckForUpdate -> {

            }
            SettingsEvent.onGithubOpen -> {
                openGithub()
            }

            SettingsEvent.onChangePhonenNameSubmit -> {
                phoneNameSubmit()

            }
            is SettingsEvent.onBluetoothPermissionChange -> {
                changeBluetoothPermission(settingsEvent.showRational)

            }
            is SettingsEvent.onNotificationPermissionChange -> {
                changeNotificationPermission(settingsEvent.showRational)

            }

            SettingsEvent.onBluetoothPermissionGrant -> {
                changeBluetoothState()
            }
            SettingsEvent.onNotificationPermissionGrant ->{
                if(!settingsUtils.isNotificationPermissionGranted()) {
                    changeNotificationState()
                }
            }
        }
    }

    private fun setInitialValues() {
        _state.update {
            it.copy(
                isNearByPermissionGranted = settingsUtils.isBluetoothPermissionGranted(),
                isNotificationPermissionGranted = sharedPref.isNotificationAllowed && settingsUtils.isNotificationPermissionGranted(),
                isDarkModeEnabled = sharedPref.isDarkModeEnabled,
                headPhonesMinimumBattery = sharedPref.minHeadphonesBattery?.toFloat() ?: 20f,
                wearOsMinimumBattery = sharedPref.minWearosBattery?.toFloat() ?: 20f,
                phoneName = sharedPref.deviceName ?: Build.MODEL

            )
        }

    }


    private fun updatePhoneName( name : String){
        _state.update {
            it.copy(
                phoneName = name
            )
        }
    }

    private fun phoneNameSubmit() = viewModelScope.launch(Dispatchers.IO){
        sharedPref.deviceName = _state.value.phoneName
        widgetRepository.updateWidget()
    }

    private fun changeMinHeadphoneBatteryLevel(value : Float) {
        _state.update {
            it.copy(
                headPhonesMinimumBattery = value.roundToInt().toFloat()
            )
        }
        sharedPref.minHeadphonesBattery = value.roundToInt().toString()
    }

    private fun changeMinWearOsBatteryLevel(value: Float){
        _state.update {
            it.copy(
                wearOsMinimumBattery = value.roundToInt().toFloat()

            )
        }
        sharedPref.minWearosBattery = value.roundToInt().toString()
    }

    private fun changeDarkMode(){
        _state.update {
            it.copy(
                isDarkModeEnabled = !it.isDarkModeEnabled
            )
        }
        if(_state.value.isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
    }

    private fun changeNotificationPermission(showRational : Boolean) = viewModelScope.launch{
        changeNotificationState()
        if(!settingsUtils.isNotificationPermissionGranted()){
            if(!showRational){
                _effect.emit(SettingsEffect.RequestNotificationPermission)
            } else {
                openSettings()
            }
        }
    }

    private fun changeBluetoothPermission(showRational : Boolean) = viewModelScope.launch{
        if(_state.value.isNearByPermissionGranted){
            changeBluetoothState()
        } else {
            if(!showRational){
                _effect.emit(SettingsEffect.RequestNertByPermission)
            } else {
                openSettings()
            }
        }
    }


    private fun openSettings(){
        viewModelScope.launch {
            _effect.emit(SettingsEffect.OpenSettings)
        }

    }

    private fun changeNotificationState() {
        _state.update { currentState ->
            val newPermissionState = if (!currentState.isNotificationPermissionGranted) {
                settingsUtils.isNotificationPermissionGranted()
            } else {
               false
            }

            currentState.copy(isNotificationPermissionGranted = newPermissionState)
        }

        sharedPref.isNotificationAllowed = _state.value.isNotificationPermissionGranted
    }


    private fun changeBluetoothState(){
        _state.update {
            it.copy(
                isNearByPermissionGranted = settingsUtils.isBluetoothPermissionGranted()
            )
        }

    }

    private fun openGithub() = viewModelScope.launch{
        _effect.emit(SettingsEffect.OpenGithub(AppConstants.GITHUB_LINK))

    }

}