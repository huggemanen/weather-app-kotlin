package com.example.weatherapp

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Coordinates(
    val lat: Double?,
    val lon: Double?
)

// Viewmodel som håller data om den senaste laddade DetailedLocation för att slippa nya api-anrop vid recompose
class DetailedLocationViewModel(query: String? = null) : ViewModel() {
    var lastUpdated by mutableStateOf<Long?>(null)
    var query by mutableStateOf(query)
    var lat by mutableStateOf<Double?>(null)
    var lon by mutableStateOf<Double?>(null)
    var weatherForecast by mutableStateOf<WeatherForecast?>(null)
    var yesterdayWeather by mutableStateOf<YesterdayWeather?>(null)
    var pendingRequests by mutableStateOf(0)

    private val api = Api()

    // Hämtar koordinater från användarens position
    private fun getLocationCoordinates(context: Context, onResult: (Coordinates) -> Unit) {
        var latResponse: Double? = null
        var lonResponse: Double? = null

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latResponse = location.latitude
                    lonResponse = location.longitude
                }
                onResult(Coordinates(latResponse, lonResponse))
            }
    }

    // Hämtar väder för kommande dagar
    private fun getWeatherForecast(q: String, onResult: (WeatherForecast?) -> Unit) {
        api.getWeatherForecast(q) { weatherForecastResponse ->
            onResult(weatherForecastResponse)
        }
    }

    // Hämtar väder från igår
    private fun getYesterdayWeather(q: String, d: String, onResult: (YesterdayWeather?) -> Unit) {
        api.getYesterdayWeather(query = q, date = d) { yesterdayWeatherResponse ->
            onResult(yesterdayWeatherResponse)
        }
    }

    // Returnerar minuter sen senaste uppdateringen av väderdata
    private fun minutesSinceLastUpdate(): Int {
        val diff = System.currentTimeMillis() - lastUpdated!!
        return (diff / 60000).toInt()
    }

    // Hämtar in väderdata om vädret ej har uppdaterats eller om det gått minst 15 minuter sedan uppdatering
    fun loadWeather(context: Context) {
        if (lastUpdated != null && minutesSinceLastUpdate() < 15) {
            return
        }

        if (query == null) {
            pendingRequests++
            getLocationCoordinates(context) { coordinates ->
                lat = coordinates.lat
                lon = coordinates.lon

                if (lat != null && lon != null) {
                    pendingRequests++
                    getWeatherForecast("$lat,$lon") { weatherForecastResponse ->
                        weatherForecast = weatherForecastResponse
                        val localTimeString = weatherForecast?.location?.localtime
                        val dateTime = LocalDateTime.parse(localTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        val yesterdayDate = dateTime.minusDays(1).format(DateTimeFormatter.ISO_DATE)

                        pendingRequests++
                        getYesterdayWeather(q = "$lat,$lon", d = yesterdayDate) { yesterdayWeatherResponse ->
                            yesterdayWeather = yesterdayWeatherResponse
                            pendingRequests--
                        }
                        pendingRequests--
                    }


                }
                pendingRequests--
            }

        } else {
            pendingRequests++
            getWeatherForecast(query!!) { weatherForecastResponse ->
                weatherForecast = weatherForecastResponse
                val localTimeString = weatherForecast?.location?.localtime
                val dateTime = LocalDateTime.parse(localTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                val yesterdayDate = dateTime.minusDays(1).format(DateTimeFormatter.ISO_DATE)

                pendingRequests++
                getYesterdayWeather(q = "$lat,$lon", d = yesterdayDate) { yesterdayWeatherResponse ->
                    yesterdayWeather = yesterdayWeatherResponse
                    pendingRequests--
                }
                pendingRequests--
            }
        }

        lastUpdated = System.currentTimeMillis()
    }
}