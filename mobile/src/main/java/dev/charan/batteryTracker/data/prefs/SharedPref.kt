package dev.charan.batteryTracker.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import dev.charan.batteryTracker.Utils.AppConstants
import dev.charan.batteryTracker.Utils.SharedPref
import dev.charan.batteryTracker.Utils.SharedPref.Companion
import androidx.core.content.edit

class SharedPref(private val context : Context) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(KEY_SHAREDPREF_KEY,Context.MODE_PRIVATE)

    companion object{
        const val KEY_SHAREDPREF_KEY="key_sharedpref_key"
    }

    var isNotificationSent
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_NOTIFICATION_SENT, true)
        }
        set(isSent) {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putBoolean(AppConstants.IS_NOTIFICATION_SENT, isSent) }
        }
    var isNotificationSentForHeadPhones
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_NOTIFICATION_SENT, true)
        }
        set(isSent) {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putBoolean(AppConstants.IS_NOTIFICATION_SENT, isSent) }
        }
    var deviceName
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.PHONE_NAME, Build.MODEL)
        }
        set(name) {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putString(AppConstants.PHONE_NAME, name) }
        }

    var minWearosBattery
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.MIN_WEAR_OS_POWER, "20")
        }
        set(int) {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putString(AppConstants.MIN_WEAR_OS_POWER, int) }
        }

    var minHeadphonesBattery
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.MIN_HEADPHONE_POWER, "20")
        }
        set(int) {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putString(AppConstants.MIN_HEADPHONE_POWER, int) }
        }

    var isNotificationAllowed
        get() =run{
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(
                AppConstants.IS_NOTIFICATION_ALLOWED,
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED)
        }
        set(value){
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putBoolean(AppConstants.IS_NOTIFICATION_ALLOWED, value) }

        }
    var isDarkModeEnabled
        get() = run {
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.IS_DARK_MODE, AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        }
        set(value){
            val sharedPreferences = context.getSharedPreferences(SharedPref.KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit() { putBoolean(AppConstants.IS_DARK_MODE, value) }
        }


}