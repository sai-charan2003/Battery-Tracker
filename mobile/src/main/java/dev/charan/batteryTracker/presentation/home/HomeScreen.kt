package dev.charan.batteryTracker.presentation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.charan.batteryTracker.presentation.home.components.BatteryInfoCard
import dev.charan.batteryTracker.presentation.home.components.BatteryLevelDisplay
import dev.charan.batteryTracker.presentation.navigation.SettingsScreenNav

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by homeViewModel.homeState.collectAsStateWithLifecycle()
    var showdropdownmenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Battery Tracker") },
                scrollBehavior = scroll,
                actions = {
                    IconButton(
                        onClick = { showdropdownmenu = true },
                        shapes = IconButtonDefaults.shapes()
                    ) {
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
                            onClick = { navHostController.navigate(SettingsScreenNav) }
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
                .padding(top = 5.dp, start = 5.dp, end = 5.dp)

        ) {
            item {
                BatteryLevelDisplay(state.batteryState)
                Spacer(Modifier.height(4.dp))
                BatteryInfoCard(state.batteryState)


            }
        }
    }
}









