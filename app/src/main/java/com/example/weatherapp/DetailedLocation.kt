package com.example.weatherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.content.edit
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlin.math.roundToInt
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
// Top delen av detailed location skärmen som visar plats och nuvarande väder
fun DetailedLocationTopWidget(weatherForecast: WeatherForecast, viewModel: DetailedLocationViewModel) {
    val settings = getSettings(LocalContext.current)
    val useCelsius = settings.getBoolean("useCelsius", true)
    var favorites by remember { mutableStateOf( (settings.getStringSet("favorites", null) ?: emptySet())) }

    Column(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF6082B6))
            .padding(top = 16.dp, start = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val locationName = weatherForecast.location.name
        val regionName = weatherForecast.location.region
        val countryName = weatherForecast.location.country

        val temp = if (useCelsius) {
            weatherForecast.current.temp_c.roundToInt().toString() + "°C"
        } else {
            weatherForecast.current.temp_f.roundToInt().toString() + "°F"
        }

        val conditionString = weatherForecast.current.condition.text
        val conditionIconUrl64 = weatherForecast.current.condition.icon
        val conditionIconUrl128 = conditionIconUrl64.replace("64x64", "128x128")

        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Text(
                    text = locationName,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                if (viewModel.query != null) {
                    val locationId = viewModel.query!!.removePrefix("id:")
                    val isFavorite = favorites.contains(locationId)
                    IconButton(
                        onClick = {
                            favorites = if (isFavorite) {
                                favorites - locationId
                            } else {
                                favorites + locationId
                            }

                            settings.edit {
                                putStringSet("favorites", favorites)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Text(
                text = if (regionName != "") "$regionName, $countryName" else countryName ,
                style = TextStyle(
                    color = Color.White
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = temp,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = conditionString,
                    style = TextStyle(
                        color = Color.White
                    )
                )
            }

            AsyncImage(
                model = "https:$conditionIconUrl128",
                contentDescription = "Weather Icon",
                modifier = Modifier.size(128.dp)
            )
        }
    }
}

@Composable
// Mittendelen av DetailedLocation skärmen som visar sammanställning för gårdagen, idag och imorgon
fun DetailedLocationDaysWidget(weatherForecast: WeatherForecast, yesterdayWeather: YesterdayWeather, navController: NavController, viewModel: DetailedLocationViewModel) {
    val settings = getSettings(LocalContext.current)
    val useCelsius = settings.getBoolean("useCelsius", true)

    val daysMap = mapOf<String, Day>(
        "Yesterday" to yesterdayWeather.forecast.forecastday[0].day,
        "Today" to weatherForecast.forecast.forecastday[0].day,
        "Tomorrow" to weatherForecast.forecast.forecastday[1].day
    )

    Row (
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF6082B6))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for ((key, value) in daysMap) {
            if (key == "Today") {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF345E9D))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            navController.navigate("details/day/$key")
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = key,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = if (key == "Today") Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                )

                AsyncImage(
                    model = "https:${value.condition.icon}",
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = if (useCelsius) value.avgtemp_c.roundToInt().toString() + "°C" else value.avgtemp_f.roundToInt().toString() + "°F",
                    style = TextStyle(
                        fontSize = 32.sp,
                        color = if (key == "Today") Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (useCelsius) value.mintemp_c.roundToInt().toString() + "°C" else value.mintemp_f.roundToInt().toString() + "°F",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = if (key == "Today") Color.White else Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "min",
                            style = TextStyle(
                                color = if (key == "Today") Color.White else Color.LightGray
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (useCelsius) value.maxtemp_c.roundToInt().toString() + "°C" else value.maxtemp_f.roundToInt().toString() + "°F",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = if (key == "Today") Color.White else Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "max",
                            style = TextStyle(
                                color = if (key == "Today") Color.White else Color.LightGray
                            )
                        )
                    }

                }
            }

            if (key == "Today") {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF345E9D))
                )
            }

        }
    }
}

@Composable
// Mittendelen av DetailedLocation skärmen som visar sammanställning för varje timme idag
fun DetailedLocationHoursWidget(weatherForecast: WeatherForecast) {
    val settings = getSettings(LocalContext.current)
    val useCelsius = settings.getBoolean("useCelsius", true)
    val use24Hour = settings.getBoolean("use24Hour", true)

    val listState = rememberLazyListState()
    val currentHour = LocalDateTime.now(ZoneId.of(weatherForecast.location.tz_id)).hour

    LaunchedEffect(Unit) {
        listState.scrollToItem(if (currentHour == 0) 0 else currentHour-1)
    }

    LazyColumn (
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF6082B6))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        itemsIndexed(weatherForecast.forecast.forecastday[0].hour) { index, hour ->

            val timeText = if (use24Hour) {
                "%02d".format(index) + ":00"
            } else {
                when (index) {
                    0 -> "12 AM"
                    in 1..11 -> "$index AM"
                    12 -> "12 PM"
                    else -> "${index - 12} PM"
                }
            }

            if (index != 0) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF345E9D))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    text = timeText,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = if (index == currentHour) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                )

                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https:${hour.condition.icon}",
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    modifier = Modifier.weight(3f),
                    text = hour.condition.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = if (index == currentHour) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    text = if (useCelsius) hour.temp_c.roundToInt().toString() + "°C" else hour.temp_f.roundToInt().toString() + "°F",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = if (index == currentHour) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                )

            }
        }
    }
}

@Composable
// All content i DetailedLocation skärmen
fun DetailedLocationContent(weatherForecast: WeatherForecast, yesterdayWeather: YesterdayWeather, navController: NavController, viewModel: DetailedLocationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailedLocationTopWidget(weatherForecast, viewModel)
        DetailedLocationDaysWidget(weatherForecast, yesterdayWeather, navController, viewModel)
        DetailedLocationHoursWidget(weatherForecast)
    }
}



@Composable
// DetailedLocation skärmen som laddar in väderdata och sedan laddar in content på skärmen
// Visas antingen på local skärmen eller när man klickar på en plats från favorites eller search
fun DetailedLocationScreen(
    navController: NavController,
    viewModel: DetailedLocationViewModel = viewModel(),
    query: String? = null
) {
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    if (query != null && viewModel.query != query) {
        viewModel.query = query
        viewModel.lastUpdated = null
    }

    LaunchedEffect(Unit) {
        viewModel.pendingRequests++
        viewModel.loadWeather(context)
        viewModel.pendingRequests--
    }

    if (!isLoading && viewModel.lastUpdated == null) {
        isLoading = true
        viewModel.pendingRequests++
    } else if (isLoading && viewModel.pendingRequests == 1) {
        isLoading = false
        viewModel.pendingRequests--
    }

    when {
        viewModel.pendingRequests != 0 -> {
            CircularProgressIndicator()
        }
        viewModel.weatherForecast == null || viewModel.yesterdayWeather == null -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No current weather data for your location could be found.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    style = TextStyle(
                        fontSize = 24.sp
                    )
                )
                Button(
                    onClick = { viewModel.loadWeather(context) }
                ) {
                    Text("Try to load weather again")
                }
            }
        }
        else -> {
            DetailedLocationContent(viewModel.weatherForecast!!, viewModel.yesterdayWeather!!, navController, viewModel)
        }
    }
}