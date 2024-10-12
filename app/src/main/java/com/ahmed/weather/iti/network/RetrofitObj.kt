package com.ahmed.weather.iti.network

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitObj {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val CACHE_SIZE = 10 * 1024 * 1024 // 10 MB

    private var onlineInterceptor: Interceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val maxAge = 60
        response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }

    private var offlineInterceptor: Interceptor = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtil.isNetworkAvailable()) {
            val maxStale = 60 * 60 * 24 * 7
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .removeHeader("Pragma")
                .build()
        }
        chain.proceed(request)
    }

    private val cache = Cache(File(MyApplication.instance.cacheDir, "http-cache"), CACHE_SIZE.toLong())

    private val client = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(offlineInterceptor)
        .addNetworkInterceptor(onlineInterceptor)
        .build()

    private val retrofitObj: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: WeatherApiService = retrofitObj.create(WeatherApiService::class.java)
}