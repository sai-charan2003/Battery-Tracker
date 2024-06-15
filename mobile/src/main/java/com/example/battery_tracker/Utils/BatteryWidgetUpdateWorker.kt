package com.example.battery_tracker.Utils

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.battery_tracker.widgets.material3.Material3widget

import com.example.battery_tracker.widgets.transparent.TransparentWidget
import kotlinx.coroutines.DelicateCoroutinesApi

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BatteryWidgetUpdateWorker(context: Context, parameterName: WorkerParameters):CoroutineWorker(context,parameterName) {
    val context=context
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun doWork(): Result {
        GlobalScope.launch {
            Material3widget.updateAll(context)
            TransparentWidget.updateAll(context)
        }
        return Result.success()
    }
    companion object {
        fun setup(){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)


                .build()
            val request= PeriodicWorkRequestBuilder<BatteryWidgetUpdateWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)

                .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                "UpdateBattery",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}