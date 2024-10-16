package com.ahmed.weather.iti.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.repository.IRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "FavouriteViewModel"
class FavouriteViewModel(private val repository: IRepository) : ViewModel() {

    private val _favList = MutableSharedFlow<List<FavouriteDTO>>()
    val favList = _favList.asSharedFlow()

    suspend fun addFav(favouriteDTO: FavouriteDTO){
        repository.addFav(favouriteDTO)
    }
    suspend fun removeFav(favouriteDTO: FavouriteDTO){
        repository.removeFav(favouriteDTO)
    }

    fun getAllFav() {
        viewModelScope.launch {
            repository.getAllFav()
                .catch {e->
                    e.printStackTrace()
                }
                .collectLatest {

                _favList.emit(it)
            }
        }
    }

}