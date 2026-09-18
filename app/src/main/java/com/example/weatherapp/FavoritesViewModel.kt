package com.example.weatherapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

data class FavoriteWeatherObject(
    val locationName: String,
    val regionName: String,
    val tempCelsius: String,
    val tempFahrenheit: String,
    val weatherString: String
)

// Viewmodel som håller data för alla favoritobjekt för att slippa nya api-anrop vid recompose när inget har ändrats
class FavoritesViewModel : ViewModel() {
    var favoritesMap by mutableStateOf<MutableMap<String, FavoriteWeatherObject?>>(mutableMapOf())
    var lastUpdated by mutableStateOf<Long?>(null)
    var pendingRequests by mutableStateOf(0)

    private val api = Api()

    // Hämtar väderdata för kommande dag
    private fun getWeatherForecast(
        location: String,
        onResult: (FavoriteWeatherObject?) -> Unit
    ) {
        api.getWeatherForecast("id:$location") { weatherForecast ->
            if (weatherForecast != null) {
                val locationName = weatherForecast.location.name
                val regionName = if (weatherForecast.location.region == "") {
                    weatherForecast.location.country
                } else {
                    "${weatherForecast.location.region}, ${weatherForecast.location.country}"
                }

                val tempCelsius = "${weatherForecast.current.temp_c.roundToInt()}°C"
                val tempFahrenheit = "${weatherForecast.current.temp_f.roundToInt()}°F"

                val weatherString = weatherForecast.current.condition.text

                onResult(
                    FavoriteWeatherObject(
                        locationName = locationName,
                        regionName = regionName,
                        tempCelsius = tempCelsius,
                        tempFahrenheit = tempFahrenheit,
                        weatherString = weatherString
                    )
                )
            } else {
                onResult(null)
            }
        }
    }

    // Returnerar minuter sedan senaste uppdateringen
    private fun minutesSinceLastUpdate(): Int {
        val diff = System.currentTimeMillis() - lastUpdated!!
        return (diff / 60000).toInt()
    }

    // Laddar väder om inget laddats eller minst 15 minuter gått sen senast och favoriter har förändrats
    fun loadWeather(favoritesUpdated: Set<String>) {
        if (lastUpdated != null && minutesSinceLastUpdate() < 15) {
            if (favoritesUpdated == favoritesMap.keys) {
                return
            } else {
                val newFavorites = favoritesUpdated - favoritesMap.keys
                val removedFavorites = favoritesMap.keys - favoritesUpdated

                for (location in removedFavorites) {
                    favoritesMap.remove(location)
                }

                for (location in newFavorites) {
                    pendingRequests++
                    getWeatherForecast(location) { favoriteWeatherObject ->
                        favoritesMap[location] = favoriteWeatherObject
                        pendingRequests--
                    }
                }
            }
        } else {
            if (favoritesUpdated != favoritesMap.keys) {
                val newFavorites = favoritesUpdated - favoritesMap.keys
                val removedFavorites = favoritesMap.keys - favoritesUpdated

                for (location in removedFavorites) {
                    favoritesMap.remove(location)
                }

                for (location in newFavorites) {
                    favoritesMap[location] = null
                }
            }

            for (location in favoritesMap.keys) {
                pendingRequests++
                getWeatherForecast(location) { favoriteWeatherObject ->
                    favoritesMap[location] = favoriteWeatherObject
                    pendingRequests--
                }
            }

            lastUpdated = System.currentTimeMillis()
        }
    }

}