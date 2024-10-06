package com.ahmed.weather.iti.network

import com.ahmed.weather.iti.Coord
import com.ahmed.weather.iti.Main
import com.ahmed.weather.iti.Sys
import com.ahmed.weather.iti.Weather
import com.ahmed.weather.iti.Wind

data class WeatherItem(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Int,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(val lon: Double, val lat: Double)
data class Weather(val id: Int, val main: String, val description: String, val icon: String)
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)
data class Wind(val speed: Double, val deg: Int)
data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Long, val sunset: Long)
