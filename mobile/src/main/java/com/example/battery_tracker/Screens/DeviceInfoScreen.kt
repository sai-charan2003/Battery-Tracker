package com.example.battery_tracker.Screens

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import java.io.File
import java.text.DecimalFormat
import java.util.Formatter


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun DevieInfoScreen(navHostController: NavHostController){
    val context= LocalContext.current
    val gigabytes = 1024.0 * 1024.0 * 1024.0

    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    val storage=android.os.Environment.getStorageDirectory()
    activityManager.getMemoryInfo(memoryInfo)
    val totalMemory = memoryInfo.totalMem/gigabytes
    var availableMemory = memoryInfo.availMem/gigabytes
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val bytesAvailable = stat.blockSize.toLong() * stat.blockCount.toLong()
    val megAvailable = bytesAvailable / 1048576



    var totalSizeBytes: Long = 0

    // Get internal storage path
    val internalPath = Environment.getDataDirectory()
    val internalStat = StatFs(internalPath.path)
    val internalBlockSize: Long = internalStat.blockSizeLong
    val internalTotalBlocks: Long = internalStat.blockCountLong
    totalSizeBytes += internalTotalBlocks * internalBlockSize
    val totalSizeGB: Double = totalSizeBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
    var availableSizeBytes: Long = 0

    // Get internal storage path


    val internalAvailableBlocks: Long = internalStat.availableBlocksLong
    val internalAvailableBytes: Long = internalAvailableBlocks * internalBlockSize
    availableSizeBytes= internalAvailableBytes.toDouble().toLong()

    val availableSizeGB: Double = availableSizeBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)


//    val totalSize: Double = (totalBlocks * blockSize)/gigabytes
    //val availableSize: Double = (availableBlocks * blockSize)/gigabytes




    Scaffold() {


        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            item {
                Text(
                    text = "Basics",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xff00b4d8)


                )

                Text(
                    text = "Model",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = android.os.Build.MODEL,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Brand",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = android.os.Build.BRAND,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Manufacturer",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = android.os.Build.MANUFACTURER,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Hardware",
                    modifier = Modifier.padding(top = 35.dp, start = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xff00b4d8)


                )
                Text(
                    text = "SOC Manufacturer",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = android.os.Build.SOC_MANUFACTURER,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "SOC Model",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = android.os.Build.SOC_MODEL,
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Cores",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = Runtime.getRuntime().availableProcessors().toString(),
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )
                Text(
                    text = "Total RAM",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                Text(
                    text = String.format("%.2f",totalMemory)+"GB",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )

                Text(
                    text = "Available Storage",
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                    fontSize = 17.sp
                )
                var total=(totalSizeGB+availableSizeGB)
                if(total>=125 && total<=128){
                    total=128.0
                }
                if(total>=495 && total<=512){
                    total=512.0
                }

                Text(
                    text = availableSizeGB.toInt().toString()+"GB",
                    modifier = Modifier.padding(top = 3.dp, start = 10.dp)
                )







            }

    }
    
    
}
    }