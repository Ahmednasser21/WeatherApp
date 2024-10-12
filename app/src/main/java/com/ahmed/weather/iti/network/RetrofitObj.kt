package com.ahmed.weather.iti.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObj {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private var onlineInterceptor: Interceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        response.newBuilder()
            .header("Cache-Control", "public, max-age=" + 30)
            .build()
    }

    private var offlineInterceptor: Interceptor = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtil.isNetworkAvailable()) {
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                .build()
        }
        chain.proceed(request)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(onlineInterceptor)
        .addInterceptor(offlineInterceptor)
        .build()

    private val retrofitObj: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: WeatherApiService = retrofitObj.create(WeatherApiService::class.java)
}