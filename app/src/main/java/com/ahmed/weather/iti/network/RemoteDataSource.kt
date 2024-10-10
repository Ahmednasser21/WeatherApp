package com.ahmed.weather.iti.network

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse

class RemoteDataSource(private val weatherApiService: WeatherApiService) {

    suspend fun getForecastData(lat: Double, lon: Double, units: String?, lang: String?): WeatherForecastResponse {
        return weatherApiService.getForecastData(lat, lon, units, lang)
    }

    suspend fun getCurrentData(lat: Double, lon: Double, units: String?, lang: String?): WeatherCurrentResponse {
        return weatherApiService.getCurrentData(lat, lon, units, lang)
    }
}