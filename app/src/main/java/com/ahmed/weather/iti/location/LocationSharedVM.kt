package com.ahmed.weather.iti.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationSharedVM : ViewModel() {

   private val _mainLocationData = MutableStateFlow(LocationData(0.0,0.0,""))
    val mainLocationData = _mainLocationData.asStateFlow()

    fun sendLocationData(locationData: LocationData){
        viewModelScope.launch{
        _mainLocationData.emit(locationData)
            }
    }
}