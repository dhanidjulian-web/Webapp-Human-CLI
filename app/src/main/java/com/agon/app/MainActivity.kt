package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.agon.app.ui.screens.ChatScreen
import com.agon.app.ui.screens.LocalGgufScreen
import com.agon.app.ui.screens.ProviderCatalogScreen
import com.agon.app.ui.screens.RagmyAgentScreen
import com.agon.app.ui.screens.SandboxScreen
import com.agon.app.ui.screens.VoiceCallScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.viewmodel.AlpineSandboxViewModel
import com.agon.app.viewmodel.ChatViewModel
import com.agon.app.viewmodel.LocalGgufViewModel
import com.agon.app.viewmodel.ProviderViewModel
import com.agon.app.viewmodel.VoiceCallViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AgonAppTheme {
                MainApp()
            }
        }
    }
}

private data class Destination(val route: String, val label: String)

private val destinations = listOf(
    Destination("chat", "KodeAI"),
    Destination("sandbox", "Sandbox"),
    Destination("providers", "Provider"),
    Destination("ragmy_agent", "Ragmy AI"),
    Destination("voice_call", "Telp AI")
)

@Composable
fun MainApp() {
    val navController = rememberNavController()

    val chatViewModel: ChatViewModel = viewModel()
    val providerViewModel: ProviderViewModel = viewModel()
    val localGgufViewModel: LocalGgufViewModel = viewModel()
    val voiceCallViewModel: VoiceCallViewModel = viewModel()
    val sandboxViewModel: AlpineSandboxViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNav(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("chat") {
                ChatScreen(
                    viewModel = chatViewModel,
                    onNavigateToProviders = { navController.navigateSingleTop("providers") }
                )
            }
            composable("sandbox") {
                SandboxScreen(viewModel = sandboxViewModel)
            }
            composable("providers") {
                ProviderCatalogScreen(
                    viewModel = providerViewModel,
                    onNavigateToChat = { navController.navigateSingleTop("chat") }
                )
            }
            composable("ragmy_agent") {
                RagmyAgentScreen()
            }
            composable("voice_call") {
                VoiceCallScreen(viewModel = voiceCallViewModel)
            }
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (destination.route) {
                            "chat" -> if (selected) Icons.Filled.Code else Icons.Outlined.Code
                            "sandbox" -> if (selected) Icons.Filled.Terminal else Icons.Outlined.Terminal
                            "providers" -> if (selected) Icons.Filled.Storage else Icons.Outlined.Storage
                            "ragmy_agent" -> if (selected) Icons.Filled.Public else Icons.Outlined.Public
                            else -> if (selected) Icons.Filled.PhoneInTalk else Icons.Outlined.PhoneInTalk
                        },
                        contentDescription = destination.label,
                    )
                },
                label = { Text(destination.label) },
                selected = selected,
                onClick = { navController.navigateSingleTop(destination.route) },
            )
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
