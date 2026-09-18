package com.example.uppgift9

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// interface med api-anropsmetoder som implementeras av apin
interface WeatherApi {
    @GET("forecast.json")
    fun getWeatherForecast(
        @Query("q") query: String,
        @Query("days") days: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Call<WeatherForecast>

    @GET("history.json")
    fun getYesterdayWeather(
        @Query("q") query: String,
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("dt") date: String = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
    ): Call<YesterdayWeather>

    @GET("search.json")
    fun getSearchLocations(
        @Query("q") query: String,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Call<List<SearchLocation>>
}

