package dev.charan.batteryTracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.charan.batteryTracker.data.Repository.BatteryInfoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val batteryInfoRepo: BatteryInfoRepo
) : ViewModel(){
    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()
    init {
        batteryInfoRepo.registerBatteryReceiver()
        getBatteryInfo()

    }

    private fun getBatteryInfo() = viewModelScope.launch{
        batteryInfoRepo.getBatteryDetails().collectLatest {
            if(it !=null) {
                _homeState.value = HomeState(it)
            }

        }


    }

    override fun onCleared() {
        batteryInfoRepo.unRegisterBatteryReceiver()
    }


}