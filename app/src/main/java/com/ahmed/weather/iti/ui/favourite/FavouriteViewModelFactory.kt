package com.ahmed.weather.iti.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmed.weather.iti.repository.Repository

class FavouriteViewModelFactory(private val repository: Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            FavouriteViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}