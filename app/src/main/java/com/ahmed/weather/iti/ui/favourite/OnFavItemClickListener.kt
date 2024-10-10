package com.ahmed.weather.iti.ui.favourite

import com.ahmed.weather.iti.database.FavouriteDTO

interface OnFavItemClickListener {
    fun onItemClick(favouriteDTO: FavouriteDTO)
}