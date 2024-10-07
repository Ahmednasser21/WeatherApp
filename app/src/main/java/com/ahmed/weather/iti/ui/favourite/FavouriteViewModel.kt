package com.ahmed.weather.iti.ui.favourite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "FavouriteViewModel"
class FavouriteViewModel(private val repository: Repository) : ViewModel() {

    private val _favList = MutableSharedFlow<List<FavouriteDTO>>()
    val favList = _favList.asSharedFlow()

    suspend fun addFav(favouriteDTO: FavouriteDTO){
        repository.addFav(favouriteDTO)
    }
    suspend fun removeFav(favouriteDTO: FavouriteDTO){
        repository.removeFav(favouriteDTO)
    }

    fun getAllFav() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFav()
                .catch {e->
                    Log.e(TAG, "getAllFav: ${e.printStackTrace()}" )
                }
                .collectLatest {
                    Log.d(TAG, "Updating UI with list: $it")
                _favList.emit(it)
            }
        }
    }

}