package com.example.battery_tracker.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class SharedPref(private val context: Context) {
    companion object{
        const val KEY_SHAREDPREF_KEY="key_sharedpref_key"
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            return instance ?: synchronized(this) {
                instance ?: SharedPref(context).also { instance = it }
            }
        }

    }
    var isNotificationSent
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_NOTIFICATION_SENT, true)
        }
        set(isSent) {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.IS_NOTIFICATION_SENT, isSent).apply()
        }
    var isNotificationSentForHeadPhones
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_NOTIFICATION_SENT, true)
        }
        set(isSent) {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.IS_NOTIFICATION_SENT, isSent).apply()
        }
    var deviceName
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.PHONE_NAME, Build.MODEL)
        }
        set(name) {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(AppConstants.PHONE_NAME, name).apply()
        }

    var minWearosBattery
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.MIN_WEAR_OS_POWER, "20")
        }
        set(int) {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(AppConstants.MIN_WEAR_OS_POWER, int).apply()
        }

    var minHeadphonesBattery
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.MIN_HEADPHONE_POWER, "20")
        }
        set(int) {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(AppConstants.MIN_HEADPHONE_POWER, int).apply()
        }

    var isNotificationAllowed
        get() =run{
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_NOTIFICATION_ALLOWED,
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED)
        }
        set(value){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.IS_NOTIFICATION_ALLOWED,value).apply()

        }
}