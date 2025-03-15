package dev.charan.batteryTracker.presentation.home

import dev.charan.batteryTracker.data.model.BatteryInfo

data class HomeState(
    val batteryState : BatteryInfo = BatteryInfo()
)
