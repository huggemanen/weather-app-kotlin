# Weather App

An Android weather application built with Kotlin and Jetpack Compose. The app uses WeatherAPI.com to provide real-time weather data and supports location-based forecasts, city search, favorites, detailed forecasts, and customizable settings.

Originally developed as a university project for the course [Programming for Mobile Units](https://www.su.se/utbildning/utbildningskatalog/ib/ib916n) at Stockholm University.

## Screenshots



## Features

* **Local Weather:** View current weather based on your device's GPS location.
* **City Search:** Search for cities worldwide and view their current weather.
* **Favorites:** Save locations for quick access.
* **Detailed Forecast:** View hourly forecasts and detailed weather information for upcoming days.
* **Customizable Settings:**

    * Celsius and Fahrenheit
    * Metric and Imperial units
    * 12-hour and 24-hour time formats
* **Persistent Storage:** Favorites and settings are stored locally on the device.

## Tech Stack

* **Kotlin**
* **Jetpack Compose** for the user interface
* **MVVM** for application architecture
* **Retrofit** and **Gson** for API communication
* **Coil** for asynchronous image loading
* **Jetpack Compose Navigation** for navigation
* **Google Play Services Location** for GPS-based location services
* **WeatherAPI.com** for weather data

## Installation

### Requirements

* Android Studio
* Android device or emulator running Android 8.0 (API 26) or higher
* A WeatherAPI.com API key

### Setup

1. Clone the repository:

```bash
git clone https://github.com/huggemanen/weather-app-kotlin.git
cd weather-app-kotlin
```

2. Open the project in Android Studio and let Gradle finish syncing.

3. Create a WeatherAPI.com account and obtain an API key.

4. Add the API key to `local.properties` in the project root:

```properties
WEATHER_API_KEY=YOUR_KEY_HERE
```

5. Run the application on an Android device or emulator.

`local.properties` contains your API key and should not be committed to the repository.
