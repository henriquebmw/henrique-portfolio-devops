package com.musicai.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musicai.app.ui.screens.AiAssistantScreen
import com.musicai.app.ui.screens.AnalyzeScreen
import com.musicai.app.ui.screens.HomeScreen
import com.musicai.app.ui.screens.SpotifySearchScreen

@Composable
fun RootNav() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Dest.Home.route

    // Secure navigation (avoids doubles)
    val navigateTo: (String) -> Unit = { route ->
        if (route != currentRoute) {
            nav.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(nav.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            MelodyBottomNav(
                selected = currentRoute,
                onSelect = navigateTo
            )
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Home.route) {
                HomeScreen(
                    onOpenSearch = { navigateTo(Dest.Search.route) },
                    onOpenAnalyze = { navigateTo(Dest.Analyze.route) },
                    onOpenAi = { navigateTo(Dest.AiAssistant.route) }
                    onOpenChat = { nav.navigate(Dest.Chat.route) }
                )
            }
            composable(Dest.Search.route) { SpotifySearchScreen() }
            composable(Dest.Analyze.route) { AnalyzeScreen() }
            composable(Dest.AiAssistant.route) { AiAssistantScreen() }
            composable(Dest.Chat.route) { ChatScreen() }
        }
    }
}
