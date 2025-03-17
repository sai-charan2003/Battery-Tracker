package dev.charan.batteryTracker.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.charan.batteryTracker.presentation.home.components.BatteryInfoCard
import dev.charan.batteryTracker.presentation.home.components.BatteryLevelDisplay

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









