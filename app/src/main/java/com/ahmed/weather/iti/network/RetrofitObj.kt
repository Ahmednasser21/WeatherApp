package com.ahmed.weather.iti.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObj {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofitObj: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: WeatherApiService = retrofitObj.create(WeatherApiService::class.java)
}