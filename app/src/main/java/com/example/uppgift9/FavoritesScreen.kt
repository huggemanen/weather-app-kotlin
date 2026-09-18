package com.example.uppgift9

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
// Visar favoritskärmen som navigeras till via navbaren i botten av skärmen
fun FavoritesScreen(viewModel: FavoritesViewModel, navController: NavController) {
    val context = LocalContext.current
    val settings = getSettings(context)
    val useCelsius = settings.getBoolean("useCelsius", true)
    val favorites = settings.getStringSet("favorites", emptySet()) ?: emptySet()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.pendingRequests++
        viewModel.loadWeather(favorites)
        viewModel.pendingRequests--
    }

    if (favorites != viewModel.favoritesMap.keys && !isLoading) {
        isLoading = true
        viewModel.pendingRequests++
    } else if (isLoading && viewModel.pendingRequests == 1) {
        isLoading = false
        viewModel.pendingRequests--
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (viewModel.pendingRequests > 0) {
            CircularProgressIndicator()
        } else {
            if (viewModel.favoritesMap.isEmpty()) {
                Text("No favorite location added.")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.favoritesMap.keys.toList()) { location ->
                    val locationData = viewModel.favoritesMap[location]

                    if (locationData != null) {
                        val locationName = locationData.locationName
                        val regionName = locationData.regionName
                        val temp = if (useCelsius) locationData.tempCelsius else locationData.tempFahrenheit
                        val weatherString = locationData.weatherString

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF6082B6))
                                .clickable(
                                    onClick = {
                                        navController.navigate("details/$location")
                                    }
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.70f)
                            ) {
                                Text(
                                    text = locationName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = regionName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        color = Color.White
                                    )
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = temp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = weatherString,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}