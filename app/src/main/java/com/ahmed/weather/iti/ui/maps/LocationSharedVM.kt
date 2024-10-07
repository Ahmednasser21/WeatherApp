package com.ahmed.weather.iti.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationSharedVM : ViewModel() {

   private val _mainLocationData = MutableStateFlow(LocationData(0.0,0.0,""))
    val mainLocationData = _mainLocationData.asStateFlow()

    private val _favLocationData = MutableStateFlow(LocationData(0.0,0.0,""))
    val favLocationData = _favLocationData.asStateFlow()

    fun sendMainLocationData(locationData: LocationData){
        viewModelScope.launch{
        _mainLocationData.emit(locationData)
            }
    }

    fun sendFavLocationData(locationData: LocationData){
        viewModelScope.launch {
            _favLocationData.emit(locationData)
        }
    }
}