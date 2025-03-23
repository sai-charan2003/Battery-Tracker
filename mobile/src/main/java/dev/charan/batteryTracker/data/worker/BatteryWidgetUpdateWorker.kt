package dev.charan.batteryTracker.data.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.charan.batteryTracker.data.repository.WidgetRepository
import dev.charan.batteryTracker.utils.AppConstants
import dev.charan.batteryTracker.widgets.Material3widget
import dev.charan.batteryTracker.widgets.TransparentWidget
import java.util.concurrent.TimeUnit

@HiltWorker
class BatteryWidgetUpdateWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            Material3widget.updateAll(context)
            TransparentWidget.updateAll(context)
            return Result.success()
        } catch (e:Exception){
            Log.d("TAG", "doWork: ${e.message}")
            return Result.failure()
        }

    }

    companion object {
        fun setup(context: Context) {
            val constraints = Constraints.Builder()
                .build()

            val request = PeriodicWorkRequestBuilder<BatteryWidgetUpdateWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)

                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AppConstants.UPDATE_BATTERY,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
        }
    }
}