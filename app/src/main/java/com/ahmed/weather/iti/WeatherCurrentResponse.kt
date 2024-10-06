package com.ahmed.weather.iti

import com.ahmed.weather.iti.network.Coord
import com.ahmed.weather.iti.network.Main
import com.ahmed.weather.iti.network.Sys
import com.ahmed.weather.iti.network.Weather
import com.ahmed.weather.iti.network.Wind
import com.google.gson.annotations.SerializedName

data class WeatherCurrentResponse  (
    val coord: Coord? = null,
    val weather: List<Weather>?= null,
    val base: String?= null,
    val main: Main?= null,
    val visibility: Long?= null,
    val wind: Wind?= null,
    val clouds: Clouds?= null,
    val dt: Long?= null,
    val sys: Sys?= null,
    val timezone: Long?= null,
    val id: Long?= null,
    val name: String?= null,
    val cod: Long?= null
)

data class Clouds (
    val all: Long
)

data class Coord (
    val lon: Double,
    val lat: Double
)

data class Main (
    var temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("temp_min")
    val tempMin: Double,

    @SerializedName("temp_max")
    val tempMax: Double,

    val pressure: Long,
    val humidity: Long,

    @SerializedName("sea_level")
    val seaLevel: Long,

    @SerializedName("grnd_level")
    val grndLevel: Long
)

data class Sys (
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind (
    val speed: Double,
    val deg: Long,
    val gust: Double
)