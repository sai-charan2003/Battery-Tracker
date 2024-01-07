//The App design is inspired from aBattery - Battery health by san_laam
//Link for the aBattery app: https://play.google.com/store/apps/details?id=me.linshen.abattery


package com.example.battery_widget

import android.Manifest
import android.annotation.SuppressLint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.battery_widget.Navigation.NavigationApphost
import com.example.battery_widget.ui.theme.Battery_widgetTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnrememberedMutableState")
       override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Battery_widgetTheme(darkTheme = isSystemInDarkTheme()) {
                           Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                               val applicationcontext=applicationContext
                               var selectedItem by remember { mutableIntStateOf(0) }
                               val items = listOf("Battery", "Deviceinfo", "Settings")
                               val navController= rememberNavController()
                               val icons = listOf(Icons.Outlined.BatteryFull, Icons.Outlined.Info, Icons.Outlined.Settings)
                               val selectedicons = listOf(Icons.Filled.BatteryFull, Icons.Filled.Info, Icons.Filled.Settings)
                               val navBackStackEntry by navController.currentBackStackEntryAsState()
                               val currentDestination = navBackStackEntry?.destination

                               if (ActivityCompat.checkSelfPermission(
                                       LocalContext.current,
                                       Manifest.permission.BLUETOOTH_CONNECT
                                   ) == PackageManager.PERMISSION_GRANTED
                               ) {

                               } else {
                                   ActivityCompat.requestPermissions(
                                       this,
                                       arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                       1
                                   )

                               }
                               val batterIntent = registerReceiver(
                                   null,
                                   IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                               )

                               if (batterIntent != null) {
                                   val Scroll= TopAppBarDefaults.pinnedScrollBehavior()
                                   Scaffold(modifier= Modifier
                                       .fillMaxSize()
                                       .nestedScroll(Scroll.nestedScrollConnection),
                                       topBar = { TopAppBar(title = { if(selectedItem==0) {Text(text = "Battery")} else if(selectedItem==1){ Text(text="Device Info")}else{ Text("Settings")} }, scrollBehavior = Scroll) },


                                       bottomBar = {
                                           NavigationBar(content = {
                                               items.forEachIndexed { index, item ->
                                                   NavigationBarItem(

                                                       selected = selectedItem == index,
                                                       onClick = {
                                                           selectedItem =
                                                               index;


                                                           navController.navigate(item){
                                                               popUpTo(navController.graph.findStartDestination().id){
                                                                   saveState=true
                                                               }
                                                               launchSingleTop=true
                                                               restoreState=true
                                                           }
                                                       },

                                                       icon = {
                                                           Icon(
                                                               imageVector = if (index == selectedItem) {
                                                                   selectedicons[index]
                                                               } else {
                                                                   icons[index]

                                                               },
                                                               contentDescription = items[index]
                                                           )

                                                       },
                                                       label = { Text(text = item)}
                                                       
                                                       )


                                               }
                                           })
                                       }




                                       ) {
                                           Box(modifier = Modifier.padding(top=it.calculateTopPadding()-40.dp, bottom = it.calculateBottomPadding())) {


                                               NavigationApphost(navController = navController)

                                           }
                                   }

                               }
                           }
            }
        }
    }
}


















