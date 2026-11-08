package com.darren.week6vp.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darren.week6vp.navigation.ClubProjectsRoute
import com.darren.week6vp.navigation.CommunitiesRoute
import com.darren.week6vp.navigation.DashboardRoute
import com.darren.week6vp.viewmodel.CampusViewModel

private data class TabItem(
    val label: String,
    val route: Any,
    val icon: ImageVector
)

@Composable
fun CampusApp(
    viewModel: CampusViewModel = viewModel()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val tabs = listOf(
        TabItem("Dashboard", DashboardRoute, Icons.Default.Home),
        TabItem("Communities", CommunitiesRoute, Icons.Default.Person)
    )

    // bottom bar only shows on the two top-level pages
    val showBottomBar = tabs.any { tab ->
        destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
    }

    // show the message from the ViewModel, then clear it
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "OK")
            viewModel.clearMessage()
        }
    }

    Scaffold(
        // each screen handles its own status bar padding
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // save and restore state so tabs are not reset
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> {
                DashboardScreen(viewModel)
            }
            composable<CommunitiesRoute> {
                CommunitiesScreen(
                    viewModel = viewModel,
                    onOpenClub = { id -> navController.navigate(ClubProjectsRoute(id)) }
                )
            }
            composable<ClubProjectsRoute> { entry ->
                val route = entry.toRoute<ClubProjectsRoute>()
                ClubProjectsScreen(
                    viewModel = viewModel,
                    communityId = route.communityId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}