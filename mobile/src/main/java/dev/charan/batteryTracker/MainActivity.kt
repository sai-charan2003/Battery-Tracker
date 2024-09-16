package dev.charan.batteryTracker

import Battery_Trackertheme
import android.Manifest
import android.annotation.SuppressLint

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat

import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController

import dev.charan.batteryTracker.Navigation.NavigationApphost


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2)
        setContent {
            Battery_Trackertheme() {
                NavigationApphost(navController = rememberNavController())
            }
        }
    }
}



