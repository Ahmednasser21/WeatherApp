package com.ahmed.weather.iti.ui.home

import WeatherForecastResponse
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.database.LocalDataSource
import com.ahmed.weather.iti.databinding.FragmentHomeBinding
import com.ahmed.weather.iti.network.RemoteDataSource
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private val sharedVM: LocationSharedVM by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var longitude = 0.0
    private var latitude = 0.0
    private var cityName = ""

    private lateinit var currentImage: ImageView
    private lateinit var city: TextView
    private lateinit var date: TextView
    private lateinit var currentDegree: TextView
    private lateinit var currentState: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var wind: TextView
    private lateinit var seaLevel: TextView
    private lateinit var visibility: TextView
    private lateinit var clouds: TextView
    private lateinit var maxMin: TextView
    private lateinit var feelsLike: TextView
    private lateinit var daysRec: RecyclerView
    private lateinit var hoursRec: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var languageSharedPreferences: SharedPreferences
    private lateinit var lang:String
    private lateinit var unitSharedPreferences: SharedPreferences
    private lateinit var unitsApi:String
    private lateinit var unit:String
    private lateinit var speed:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val factory = HomeViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(RetrofitObj.service),
                LocalDataSource(DataBase.getInstance(requireContext()))
            )
        )
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseUI()
        startUpdatingTime()
        getRemoteData()
        makeHourlyList()
        makeDailyList()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitDialog()
                }
            }
        )
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Exit App")
            setMessage("Do you really want to exit the app?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }


    private fun initialiseUI() {
        dailyAdapter = DailyAdapter()
        hourlyAdapter = HourlyAdapter()
        languageSharedPreferences = requireActivity().getSharedPreferences(getString(R.string.language),Context.MODE_PRIVATE)
        unitSharedPreferences = requireActivity().getSharedPreferences(getString(R.string.temperature),Context.MODE_PRIVATE)
        currentImage = binding.imgCurrentIcon
        city = binding.txtCity
        date = binding.txtDate
        currentDegree = binding.tvCurrentDegree
        currentState = binding.tvCurrentStatus
        pressure = binding.tvPressure
        humidity = binding.tvHumidity
        wind = binding.tvWind
        seaLevel = binding.tvSeaLevel
        visibility = binding.visibility
        clouds = binding.clouds
        maxMin = binding.tvMaxMin
        feelsLike = binding.tvFeelsLike
        lang = if(languageSharedPreferences.getBoolean("arabic",false)){
            "ar"
        }else{"en"}

        unitsApi = if (unitSharedPreferences.getString(getString(R.string.temperature),"celsius") == "celsius"){
            unit = "°C"
            speed = getString(R.string.meter_s)
            "metric"

        }else if(unitSharedPreferences.getString(getString(R.string.temperature),"celsius") == "kalvin"){
            unit = "°K"
            speed = getString(R.string.meter_s)
            "standard"
        }else{
            unit = "°F"
            speed = getString(R.string.mile_h)
            "imperial"
        }


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

    private fun getRemoteData() {
        lifecycleScope.launch {
            val isFav = arguments?.let { HomeFragmentArgs.fromBundle(it).fav } ?: false
            if (isFav) {
                sharedVM.mainLocationData.collect {
                    longitude = it.longitude
                    latitude = it.latitude
                    cityName = it.cityName
                    city.text = it.cityName.substringBefore("}")
                    homeViewModel.getWeatherForecast(longitude, latitude, unitsApi, lang)
                    homeViewModel.getCurrentWeather(longitude, latitude, unitsApi, lang)
                }
            } else {
                val prefs =
                    requireActivity().getSharedPreferences("locationData", Context.MODE_PRIVATE)
                longitude = prefs.getFloat("longitude", 0.0f).toDouble()
                latitude = prefs.getFloat("latitude", 0.0f).toDouble()
                cityName = prefs.getString("address_line", "NorthSinai").toString()
                city.text = cityName.substringBefore("}")
                homeViewModel.getWeatherForecast(longitude, latitude, unitsApi, lang)
                homeViewModel.getCurrentWeather(longitude, latitude, unitsApi, lang)
            }
        }
    }

    private fun makeHourlyList() {
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
                                getWeatherIcon(weatherData.weather[0].icon),
                                "${weatherData.main.temp}$unit"
                            )
                        }
                        hourlyAdapter.submitList(hourlyList)

                        val dailyList = createDailyList(weatherForecast)
                        dailyAdapter.submitList(dailyList)
                    }

                    is DataState.OnFailed -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun makeDailyList() {
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
                        currentImage.setImageResource(
                            getWeatherIcon(
                                currentWeather.weather?.get(
                                    0
                                )?.icon ?: ""
                            )
                        )
                        currentDegree.text = "${currentWeather.main?.temp}$unit"
                        currentState.text = currentWeather.weather?.get(0)?.description
                        pressure.text = "${currentWeather.main?.pressure}${getString(R.string.pressure_unit)}"
                        visibility.text = currentWeather.visibility.toString()
                        wind.text = "${currentWeather.wind?.speed}$speed"
                        humidity.text = "${currentWeather.main?.humidity}%"
                        clouds.text = "${currentWeather.clouds?.all}%"
                        seaLevel.text = "${currentWeather.main?.seaLevel}${getString(R.string.sea_level_unit)}"
                        feelsLike.text = "Feels like ${currentWeather.main?.feelsLike}$unit"
                        maxMin.text =
                            "${currentWeather.main?.tempMax}$unit/${currentWeather.main?.tempMin}$unit"
                    }

                    is DataState.OnFailed -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
                delay(60000) // Update every minute
            }
        }
    }

    private fun getDayName(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        val today = LocalDate.now()

        return when {
            date.isEqual(today) -> getString(R.string.today)
            else -> date.format(DateTimeFormatter.ofPattern("EEE"))
        }
    }

    private fun getHour(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        return outputFormat.format(date as Date)
    }

    private fun createDailyList(weatherForecast: WeatherForecastResponse): List<DailyDTO> {
        return weatherForecast.list.groupBy { it.dtTxt.substring(0, 10) }
            .values.take(5).map { dailyData ->
                val firstEntry = dailyData.first()
                DailyDTO(
                    day = getDayName(firstEntry.dtTxt.substring(0, 10)),
                    img = getWeatherIcon(firstEntry.weather[0].icon),
                    status = "${firstEntry.weather[0].description}",
                    minMax = "${dailyData.maxOf { it.main.tempMax }}$unit / ${dailyData.minOf { it.main.tempMin }}$unit"
                )
            }
    }

    private fun getWeatherIcon(icon: String): Int {
        val iconValue: Int
        when (icon) {
            "01d" -> iconValue = R.drawable.sunny
            "01n" -> iconValue = R.drawable.moon
            "02d" -> iconValue = R.drawable.cloudy
            "02n" -> iconValue = R.drawable.cloudy_night
            "03n" -> iconValue = R.drawable.cloudy_night
            "03d" -> iconValue = R.drawable.cloudy
            "04d" -> iconValue = R.drawable.cloudy
            "04n" -> iconValue = R.drawable.cloudy_night
            "09d" -> iconValue = R.drawable.rain
            "09n" -> iconValue = R.drawable.rain
            "10d" -> iconValue = R.drawable.rain
            "10n" -> iconValue = R.drawable.rain
            "11d" -> iconValue = R.drawable.storm
            "11n" -> iconValue = R.drawable.storm
            "13d" -> iconValue = R.drawable.snow
            "13n" -> iconValue = R.drawable.snow
            "50d" -> iconValue = R.drawable.mist
            "50n" -> iconValue = R.drawable.mist
            else -> iconValue = R.drawable.windy_weather
        }
        return iconValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
