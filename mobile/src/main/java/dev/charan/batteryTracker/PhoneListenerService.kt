package dev.charan.batteryTracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.updateAll
import dev.charan.batteryTracker.widgets.Material3widget
import dev.charan.batteryTracker.widgets.TransparentWidget
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.charan.batteryTracker.data.prefs.SharedPref
import dev.charan.batteryTracker.data.repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.repository.impl.BatteryInfoRepoImp
import dev.charan.batteryTracker.utils.convertToJsonString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhoneListenerService: WearableListenerService() {
    private val scope = CoroutineScope(Dispatchers.IO)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        val batteryInfoRepo = BatteryInfoRepoImp(applicationContext, sharedPref = SharedPref(applicationContext))
        val batteryData = batteryInfoRepo.getPhoneBatteryData().convertToJsonString()
        scope.launch {
            getNodes(applicationContext).forEach { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    MESSAGE_PATH,
                    batteryData.toByteArray()
                ).apply {
                    addOnSuccessListener {
                        Log.d(TAG, "onSuccess: Data send success")
                    }
                    addOnFailureListener {
                        Log.d(TAG, "onFail: Unable to send the data $it")
                    }
                }
            }

        }

    }
    companion object{
        private const val TAG = "PhoneListenerService"
        private const val MESSAGE_PATH = "/deploy"
    }
}
private fun getNodes(context: Context): Collection<String> {
    return Tasks.await(Wearable.getNodeClient(context).connectedNodes).map { it.id }
}
