package dev.charan.batteryTracker.presentation.home

import android.hardware.BatteryState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.charan.batteryTracker.presentation.home.components.BodyText
import dev.charan.batteryTracker.presentation.home.components.TitleText
import dev.charan.batteryTracker.Utils.SharedPref
import dev.charan.batteryTracker.data.model.BatteryInfo
import dev.charan.batteryTracker.presentation.home.components.BatteryInfoCard
import dev.charan.batteryTracker.presentation.home.components.BatteryLevelDisplay
import dev.charan.batteryTracker.presentation.home.components.BatteryProgressIndicator
import dev.charan.batteryTracker.presentation.home.components.DetailsInfoRow
import dev.charan.batteryTracker.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by homeViewModel.homeState.collectAsState()
    var showdropdownmenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Battery Tracker") },
                scrollBehavior = scroll,
                actions = {
                    IconButton(onClick = { showdropdownmenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "more"
                        )
                    }
                    DropdownMenu(
                        expanded = showdropdownmenu,
                        onDismissRequest = { showdropdownmenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { navHostController.navigate(dev.charan.batteryTracker.Navigation.Destination.settings.route) }
                        )

                    }


                }

            )
        }

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)

        ) {
            item {
                BatteryLevelDisplay(state.batteryState)
                BatteryInfoCard(state.batteryState)


            }
        }
    }
}









