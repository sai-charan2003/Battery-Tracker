package dev.charan.batteryTracker.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.charan.batteryTracker.data.Repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.model.BatteryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val batteryRepository: BatteryInfoRepo
) : ViewModel() {

    private val _state = MutableStateFlow(WidgetState())
    val state = _state.asStateFlow()

    init {
        getDeviceBatteryDetails()
        batteryRepository.registerWearOsBatteryReceiver()
        batteryRepository.registerBluetoothBatteryReceiver()
        batteryRepository.registerBatteryReceiver()
    }


    private fun getDeviceBatteryDetails() = viewModelScope.launch{
        batteryRepository.getBatteryDetails().collectLatest { batteryInfo ->
            _state.update {
                it.copy(
                    batteryInfo = batteryInfo ?: BatteryInfo()
                )

            }
        }
    }

    override fun onCleared() {
        batteryRepository.unRegisterBatteryReceiver()
    }





}