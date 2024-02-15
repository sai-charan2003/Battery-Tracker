package com.example.battery_tracker

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.battery_tracker.widgets.material3.Material3widget

import com.example.battery_tracker.widgets.transparent.transparent

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Parameter

class workmanager(context: Context,parameterName: WorkerParameters):CoroutineWorker(context,parameterName) {
    val context=context
    override suspend fun doWork(): Result {
        GlobalScope.launch {
            Material3widget.updateAll(context)
            transparent.updateAll(context)
        }
        return Result.success()
    }
}