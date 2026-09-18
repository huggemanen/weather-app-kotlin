package com.example.uppgift9

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class Api {
    val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val api: WeatherApi = retrofit.create(WeatherApi::class.java)

    // Hämtar väder för de kommande dagarna
    fun getWeatherForecast(query: String, daysInput: Int = 2, callback: (WeatherForecast?) -> Unit) {
        val days = daysInput.coerceIn(0, 2) // Api:n tillåter högst 2 dagars forecast
        val call = api.getWeatherForecast(query = query, days = days)

        call.enqueue(object : Callback<WeatherForecast> {
            override fun onResponse(
                call: Call<WeatherForecast?>,
                response: Response<WeatherForecast?>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body() as WeatherForecast
                    callback(responseBody)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<WeatherForecast?>, t: Throwable) {
                callback(null)
            }
        })
    }

    // Hämtar väder från igår
    fun getYesterdayWeather(query: String, date: String, callback: (YesterdayWeather?) -> Unit) {
        val call = api.getYesterdayWeather(query = query, date = date)

        call.enqueue(object : Callback<YesterdayWeather> {
            override fun onResponse(
                call: Call<YesterdayWeather?>,
                response: Response<YesterdayWeather?>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body() as YesterdayWeather
                    callback(responseBody)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<YesterdayWeather?>, t: Throwable) {
                callback(null)
            }
        })
    }

    // Hämtar en lista av locations baserad på en söksträng
    fun getSearchLocations(query: String, callback: (List<SearchLocation>?) -> Unit) {
        val call = api.getSearchLocations(query = query)

        call.enqueue(object: Callback<List<SearchLocation>> {
            override fun onResponse(
                call: Call<List<SearchLocation>?>,
                response: Response<List<SearchLocation>?>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body() as List<SearchLocation>
                    callback(responseBody)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<SearchLocation>?>, t: Throwable) {
                callback(null)
            }
        })

    }
}