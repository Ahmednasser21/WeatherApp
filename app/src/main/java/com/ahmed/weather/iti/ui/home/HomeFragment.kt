package com.ahmed.weather.iti.ui.home

import WeatherForecastResponse
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.databinding.FragmentHomeBinding
import com.ahmed.weather.iti.ui.maps.LocationSharedVM
import com.ahmed.weather.iti.network.RetrofitObj
import com.ahmed.weather.iti.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var homeViewModel: HomeViewModel
    private val sharedVM: LocationSharedVM by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private var longitude = 0.0
    private var latitude = 0.0
    private var cityName = ""
    private val binding get() = _binding!!
    private lateinit var city: TextView
    private lateinit var date: TextView
    private lateinit var currentDegree: TextView
    private lateinit var currentState: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var wind: TextView
    private lateinit var seaLevel: TextView
    private lateinit var visiblity: TextView
    private lateinit var clouds: TextView
    private lateinit var maxMin: TextView
    private lateinit var feelsLike: TextView
    private lateinit var daysRec: RecyclerView
    private val dailyAdapter = DailyAdapter()
    private lateinit var hoursRec: RecyclerView
    private val hourlyAdapter = HourlyAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = HomeViewModelFactory(
            Repository.getInstance(
                RetrofitObj,
                DataBase.getInstance(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseUI()
        startUpdatingTime()

        lifecycleScope.launch {
//            sharedVM.mainLocationData.collect {
//                longitude = it.longitude
//                latitude = it.latitude
//                cityName = it.cityName
//                city.text = it.cityName.substringBefore("}")
//                homeViewModel.getWeatherForecast(longitude, latitude, "metric", "en")
//                homeViewModel.getCurrentWeather(longitude, latitude, "metric", "en")
//                Log.i(TAG, "onViewCreated: ${it.latitude} \n ${it.longitude} ${it.cityName}")
//            }
            val prefs = requireActivity().getSharedPreferences("locationData", Context.MODE_PRIVATE)
            longitude = prefs.getFloat("longitude",0.0f).toDouble()
            latitude = prefs.getFloat("latitude",0.0f).toDouble()
            cityName = prefs.getString("address_line", "NorthSinai").toString()
            city.text =cityName.substringBefore("}")
            homeViewModel.getWeatherForecast(longitude, latitude, "metric", "en")
            homeViewModel.getCurrentWeather(longitude, latitude, "metric", "en")


        }
        lifecycleScope.launch {
            homeViewModel.forecast.collectLatest { result ->
                when (result) {
                    is DataState.Loading -> Toast.makeText(
                        requireContext(),
                        "Loading",
                        Toast.LENGTH_SHORT
                    ).show()

                    is DataState.OnSuccess<*> -> {
                        val weatherForecast = result.data as WeatherForecastResponse
                        val hourlyList = weatherForecast.list.take(9).map { weatherData ->
                            HourlyDTO(
                                getHour(weatherData.dtTxt),
                                (R.drawable.sunny),
                                "${weatherData.main.temp}°K"
                            )
                        }
                        hourlyAdapter.submitList(hourlyList)
                        val dailyList = weatherForecast.list.groupBy { it.dtTxt.substring(0, 10) }
                            .values.take(5).mapIndexed { index, dailyData ->
                                val firstEntry = dailyData.first()
                                DailyDTO(
                                    day = getDayName(firstEntry.dtTxt.substring(0, 10), index),
                                    img = R.drawable.wind,
                                    status = "${firstEntry.weather[0].description}",
                                    minMax = "${dailyData.minOf { it.main.temp }}°K / ${dailyData.maxOf { it.main.temp }}°K"
                                )
                            }
                        dailyAdapter.submitList(dailyList)
                        Log.i(TAG, "Hourly List: $hourlyList")
                        Log.i(TAG, "Daily List: $dailyList")
                    }

                    is DataState.OnFailed -> {
                        Log.e(TAG, "forecast: ${result.msg}")
                        Toast.makeText(requireContext(), "Failed to get data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            homeViewModel.current.collectLatest { result ->
                when (result) {
                    is DataState.Loading -> Toast.makeText(
                        requireContext(),
                        "Loading",
                        Toast.LENGTH_SHORT
                    ).show()

                    is DataState.OnSuccess<*> -> {
                        val currentWeather = result.data as WeatherCurrentResponse
                        currentDegree.text = "${currentWeather.main?.temp}°K"
                        currentState.text = currentWeather.weather?.get(0)?.description
                        pressure.text = "${currentWeather.main?.pressure}hpa"
                        visiblity.text = currentWeather.visibility.toString()
                        wind.text = "${currentWeather.wind?.speed}mile/H"
                        humidity.text = "${currentWeather.main?.humidity}%"
                        clouds.text = "${currentWeather.clouds?.all}%"
                        seaLevel.text = "${currentWeather.main?.seaLevel}pa"
                        feelsLike.text = "Feels like  ${currentWeather.main?.feelsLike}°K"
                        maxMin.text =
                            "${currentWeather.main?.tempMax}°K/${currentWeather.main?.tempMin}°K"

                        Log.i(TAG, "current: ${currentWeather.main}")
                    }

                    is DataState.OnFailed -> {
                        Log.e(TAG, "current: ${result.msg}")
                        Toast.makeText(requireContext(), "Failed to get data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun initialiseUI() {
        city = binding.txtCity
        date = binding.txtDate
        currentDegree = binding.tvCurrentDegree
        currentState = binding.tvCurrentStatus
        pressure = binding.tvPressure
        humidity = binding.tvHumidity
        wind = binding.tvWind
        seaLevel = binding.tvSeaLevel
        visiblity = binding.visibility
        clouds = binding.clouds
        maxMin = binding.tvMaxMin
        feelsLike = binding.tvFeelsLike
        hoursRec = binding.recHours.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyAdapter
        }
        daysRec = binding.recDays.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyAdapter

        }
    }

    private fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("EEE, d MMMM h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun startUpdatingTime() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                date.text = getFormattedDate()
                delay(60000)
            }
        }
    }

    private fun getDayName(dateString: String, position: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (position) {
            0 -> "Tomorrow"
            else -> SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        }
    }

    private fun getHour(dateTimeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateTimeString)
        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return hourFormat.format(date)
    }


        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}