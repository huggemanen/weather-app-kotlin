package com.example.weatherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
// Skapa en rad med två rutor på en rad med rubrik och text
fun DetailedDayRow(
    title1: String,
    text1: String,
    title2: String,
    text2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title1,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = text1,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title2,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = text2,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
// Skapa DetailedDay-skärmen
fun DetailedDayScreen(day: String?, viewModel: DetailedLocationViewModel) {
    val settings = getSettings(LocalContext.current)
    val useCelsius = settings.getBoolean("useCelsius", true)
    val useMetric = settings.getBoolean("useMetric", true)
    val use24Hour = settings.getBoolean("use24Hour", true)

    val locationName = viewModel.weatherForecast!!.location.name
    val regionName = viewModel.weatherForecast!!.location.region
    val countryName = viewModel.weatherForecast!!.location.country

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val weatherDay = when (day) {
            "Yesterday" -> viewModel.yesterdayWeather!!.forecast.forecastday[0]
            "Today" -> viewModel.weatherForecast!!.forecast.forecastday[0]
            else -> viewModel.weatherForecast!!.forecast.forecastday[1]
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day ?: "",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = weatherDay.date,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(top = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            val temp = if (useCelsius) {
                weatherDay.day.avgtemp_c.roundToInt().toString() + "°C"
            } else {
                weatherDay.day.avgtemp_f.roundToInt().toString() + "°F"
            }

            val conditionString = weatherDay.day.condition.text
            val conditionIconUrl64 = weatherDay.day.condition.icon
            val conditionIconUrl128 = conditionIconUrl64.replace("64x64", "128x128")

            Column() {
                Text(
                    text = locationName,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
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

        DetailedDayRow(
            title1 = "Min. Temp",
            text1 = if (useCelsius) weatherDay.day.mintemp_c.roundToInt().toString() + "°C" else weatherDay.day.mintemp_f.roundToInt().toString() + "°F",
            title2 = "Max. Temp",
            text2 = if (useCelsius) weatherDay.day.maxtemp_c.roundToInt().toString() + "°C" else weatherDay.day.maxtemp_f.roundToInt().toString() + "°F"
        )

        DetailedDayRow(
            title1 = "Max. Wind",
            text1 = if (useMetric) weatherDay.day.maxwind_kph.roundToInt().toString() + " kph" else weatherDay.day.maxwind_mph.roundToInt().toString() + " mph",
            title2 = "Precipitation",
            text2 = if (useMetric) weatherDay.day.totalprecip_mm.roundToInt().toString() + " mm" else weatherDay.day.totalprecip_in.roundToInt().toString() + " in"
        )

        DetailedDayRow(
            title1 = "UV",
            text1 = weatherDay.day.uv.toString(),
            title2 = "Humidity",
            text2 = "${weatherDay.day.avghumidity}%"
        )

        DetailedDayRow(
            title1 = "Rain prob.",
            text1 = "${weatherDay.day.daily_chance_of_rain}%",
            title2 = "Snow prob.",
            text2 = "${weatherDay.day.daily_chance_of_snow}%"
        )

        val sunrise = if (use24Hour) {
            LocalTime.parse(weatherDay.astro.sunrise, DateTimeFormatter.ofPattern("hh:mm a")).format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            weatherDay.astro.sunrise
        }

        val sunset = if (use24Hour) {
            LocalTime.parse(weatherDay.astro.sunset, DateTimeFormatter.ofPattern("hh:mm a")).format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            weatherDay.astro.sunset
        }

        DetailedDayRow(
            title1 = "Sunrise",
            text1 = sunrise,
            title2 = "Sunset",
            text2 = sunset
        )
    }
}