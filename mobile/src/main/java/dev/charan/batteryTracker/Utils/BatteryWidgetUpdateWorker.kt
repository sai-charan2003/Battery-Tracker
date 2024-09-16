package dev.charan.batteryTracker.Utils

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.charan.batteryTracker.widgets.Material3widget

import dev.charan.batteryTracker.widgets.TransparentWidget
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
        fun setup(context: Context){
            val constraints = Constraints.Builder()
                .build()
            val request= PeriodicWorkRequestBuilder<BatteryWidgetUpdateWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)

                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AppConstants.UPDATE_BATTERY,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }


}