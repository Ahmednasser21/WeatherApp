package com.ahmed.weather.iti.network

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse

class FakeWeatherApiService : WeatherApiService {

    var forecastResponse: WeatherForecastResponse? = null
    var currentResponse: WeatherCurrentResponse? = null
    var shouldThrowError: Boolean = false

    override suspend fun getForecastData(
        lat: Double, lon: Double, units: String?, lang: String?, apiKey: String
    ): WeatherForecastResponse {
        if (shouldThrowError) {
            throw Exception("Fake network error")
        }
        return forecastResponse ?: throw Exception("No forecast response set")
    }

    override suspend fun getCurrentData(
        lat: Double, lon: Double, units: String?, lang: String?, apiKey: String
    ): WeatherCurrentResponse {
        if (shouldThrowError) {
            throw Exception("Fake network error")
        }
        return currentResponse ?: throw Exception("No current response set")
    }
}

class FakeRemoteDataSource(private val fakeWeatherApiService: FakeWeatherApiService) {

    suspend fun getForecastData(
        lat: Double, lon: Double, units: String? = null, lang: String? = null
    ): WeatherForecastResponse {
        return fakeWeatherApiService.getForecastData(lat, lon, units, lang, "fake-api-key")
    }

    suspend fun getCurrentData(
        lat: Double, lon: Double, units: String? = null, lang: String? = null
    ): WeatherCurrentResponse {
        return fakeWeatherApiService.getCurrentData(lat, lon, units, lang, "fake-api-key")
    }
}