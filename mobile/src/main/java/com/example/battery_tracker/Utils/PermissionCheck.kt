package com.example.battery_tracker.Utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionCheck {
    fun notificationPermissionEnabled(context:Context): Boolean{
        return ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED
    }

    fun nearbyDevicePermissionEnabled(context:Context): Boolean{
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)== PackageManager.PERMISSION_GRANTED
    }
}