package com.ahmed.weather.iti.ui.home

import WeatherForecastResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.databinding.FragmentHomeBinding
import com.ahmed.weather.iti.location.LocationSharedVM
import com.ahmed.weather.iti.network.RetrofitObj
import com.ahmed.weather.iti.repository.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object{
        private const val TAG = "HomeFragment"
    }

    private lateinit var homeViewModel:HomeViewModel
    val sharedVM: LocationSharedVM by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private var longitude = 0.0
    private var latitude = 0.0
    private var cityName = ""
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = HomeViewModelFactory(Repository.getInstance(RetrofitObj))
        homeViewModel = ViewModelProvider(this,factory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            sharedVM.mainLocationData.collect{
                longitude = it.longitude
                latitude= it.latitude
                cityName = it.cityName
                homeViewModel.getWeatherForecast(longitude,latitude,"standard","en")
                homeViewModel.getCurrentWeather(longitude,latitude,"standard","en")
                Log.i(TAG, "onViewCreated: ${it.latitude} \n ${it.longitude} ${it.cityName}")
            }

        }
        lifecycleScope.launch {
            homeViewModel.forecast.collectLatest {result->
                when(result){
                    is DataState.Loading ->Toast.makeText(requireContext(),"Loading", Toast.LENGTH_SHORT).show()
                    is DataState.OnSuccess<*>->{
                        val x = result.data as WeatherForecastResponse
                        Log.i(TAG, "forecast: ${x.city}")
                    }
                    is DataState.OnFailed->{
                        Log.e(TAG, "forecast: ${result.msg}", )
                        Toast.makeText(requireContext(),"Failed to get data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            homeViewModel.current.collectLatest {result->
                when(result){
                    is DataState.Loading ->Toast.makeText(requireContext(),"Loading", Toast.LENGTH_SHORT).show()
                    is DataState.OnSuccess<*>->{
                        val x = result.data as WeatherCurrentResponse
                        Log.i(TAG, "current: ${x.main}")
                    }
                    is DataState.OnFailed->{
                        Log.e(TAG, "current: ${result.msg}", )
                        Toast.makeText(requireContext(),"Failed to get data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}