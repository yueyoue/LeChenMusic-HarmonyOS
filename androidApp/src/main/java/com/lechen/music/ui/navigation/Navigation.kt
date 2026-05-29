package com.lechen.music.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lechen.music.ui.screen.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "首页", Icons.Default.Home)
    data object Favorites : Screen("favorites", "收藏", Icons.Default.Favorite)
    data object Search : Screen("search", "搜索", Icons.Default.Search)
    data object Artists : Screen("artists", "歌手", Icons.Default.Person)
    data object Albums : Screen("albums", "专辑", Icons.Default.Album)
    data object Login : Screen("login", "登录", Icons.Default.Login)
    data object Player : Screen("player", "播放", Icons.Default.PlayArrow)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Favorites,
    Screen.Search,
    Screen.Artists,
    Screen.Albums,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeChenNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
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
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController) }
            composable(Screen.Search.route) { SearchScreen(navController) }
            composable(Screen.Artists.route) { ArtistsScreen(navController) }
            composable(Screen.Albums.route) { AlbumsScreen(navController) }
            composable(Screen.Player.route) { PlayerScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}
