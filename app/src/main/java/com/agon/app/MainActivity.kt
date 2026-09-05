package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agon.app.di.AppContainer
import com.agon.app.ui.screens.DashboardScreen
import com.agon.app.ui.screens.PoliciesScreen
import com.agon.app.ui.screens.ProfilesScreen
import com.agon.app.ui.screens.SandboxScreen
import com.agon.app.ui.screens.SettingsScreen
import com.agon.app.ui.screens.TerminalScreen
import com.agon.app.ui.screens.VaultScreen
import com.agon.app.ui.theme.AgonAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val app = application as MusGoApp
        setContent {
            AgonAppTheme {
                MusGoRoot(container = app.container)
            }
        }
    }
}

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val mainTabs = listOf(
    TabItem("dashboard", "Home", Icons.Default.Dashboard),
    TabItem("sandbox", "Sandbox", Icons.Default.Dns),
    TabItem("vault", "Vault", Icons.Default.Key),
    TabItem("profiles", "Agents", Icons.Default.SmartToy),
    TabItem("policies", "Policy", Icons.Default.Policy),
    TabItem("settings", "System", Icons.Default.Settings),
)

@Composable
fun MusGoRoot(container: AppContainer) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute.startsWith("terminal").not()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                MusGoBottomBar(navController = navController, currentRoute = currentRoute)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("dashboard") {
                DashboardScreen(container = container)
            }
            composable("sandbox") {
                SandboxScreen(
                    container = container,
                    onOpenTerminal = { sessionId ->
                        navController.navigate("terminal/$sessionId")
                    },
                )
            }
            composable("vault") {
                VaultScreen(container = container)
            }
            composable("profiles") {
                ProfilesScreen(container = container)
            }
            composable("policies") {
                PoliciesScreen(container = container)
            }
            composable("settings") {
                SettingsScreen()
            }
            composable(
                route = "terminal/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType },
                ),
            ) { entry ->
                val sessionId = entry.arguments?.getLong("sessionId") ?: 0L
                TerminalScreen(
                    sessionId = sessionId,
                    container = container,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun MusGoBottomBar(
    navController: NavHostController,
    currentRoute: String?,
) {
    NavigationBar {
        mainTabs.forEach { tab ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = tab.icon, contentDescription = tab.label)
                },
                label = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
