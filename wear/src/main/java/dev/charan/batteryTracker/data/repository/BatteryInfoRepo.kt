package dev.charan.batteryTracker.data.repository

import dev.charan.batteryTracker.data.model.BatteryInfo

interface BatteryInfoRepo {

    fun getBatteryDetails() : BatteryInfo
}