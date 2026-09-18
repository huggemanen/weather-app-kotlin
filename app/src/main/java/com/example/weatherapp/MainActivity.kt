package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapp.ui.theme.Uppgift9Theme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.core.content.edit
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Uppgift9Theme() {
                App()
            }
        }
    }
}

@Composable
// Hela app-composablen
// Initierar lokala inställningar om ej finns, skapar en navcontroller och visar LocalScreen från start
fun App() {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val settings = getSettings(context)
    if (!settings.contains("init")) {
        settings.edit {
            putBoolean("init", true)
            putBoolean("useCelsius", true)
            putBoolean("useMetric", true)
            putBoolean("use24Hour", true)
            putStringSet("favorites", null)
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val favoritesViewModel: FavoritesViewModel = viewModel()
    val detailedLocationViewModel: DetailedLocationViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar() {
                NavigationBarItem(
                    selected = (currentDestination == "local"),
                    onClick = {
                        navController.navigate("local")
                    },
                    icon = { Icon(Icons.Default.LocationOn, "Local") }
                )
                NavigationBarItem(
                    selected = (currentDestination == "favorites"),
                    onClick = {
                        navController.navigate("favorites")
                    },
                    icon = { Icon(Icons.Default.Favorite, "Favorites") }
                )
                NavigationBarItem(
                    selected = (currentDestination == "search"),
                    onClick = {
                        navController.navigate("search")
                    },
                    icon = { Icon(Icons.Default.Search, "Search") }
                )
                NavigationBarItem(
                    selected = (currentDestination == "settings"),
                    onClick = {
                        navController.navigate("settings")
                    },
                    icon = { Icon(Icons.Default.Settings, "Settings") }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
        ) {
            NavHost(
                navController = navController,
                startDestination = "local"
            ) {
                composable("local") {
                    LocalScreen(
                        detailedLocationViewModel,
                        navController
                    )
                }

                composable("favorites") { FavoritesScreen(favoritesViewModel, navController) }
                composable("details/{location}") { backStackEntry ->
                    val location = backStackEntry.arguments?.getString("location")
                    DetailedLocationScreen(query = "id:$location", navController = navController, viewModel = detailedLocationViewModel)
                }
                composable("details/day/{day}") { backStackEntry ->
                    val day = backStackEntry.arguments?.getString("day")
                    DetailedDayScreen(day, detailedLocationViewModel)

                }
                composable("search") { SearchScreen(navController) }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}