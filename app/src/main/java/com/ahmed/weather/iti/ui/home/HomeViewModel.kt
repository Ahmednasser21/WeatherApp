package com.ahmed.weather.iti.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.weather.iti.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private var _forecast = MutableStateFlow<DataState>(DataState.Loading)
    val forecast: StateFlow<DataState> = _forecast
    private var _current = MutableStateFlow<DataState>(DataState.Loading)
    val current: StateFlow<DataState> = _current

    fun getWeatherForecast(longitude: Double, latitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherForecast(longitude, latitude, units, lang)
                .catch { e ->
                    _forecast.value = DataState.OnFailed(e)
                }
                .collect { data -> _forecast.value = DataState.OnSuccess(data) }
        }
    }

    fun getCurrentWeather(longitude: Double, latitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeather(longitude, latitude, units, lang)
                .catch { e ->
                    _current.value = DataState.OnFailed(e)
                }
                .collect { data -> _current.value = DataState.OnSuccess(data) }
        }
    }
}