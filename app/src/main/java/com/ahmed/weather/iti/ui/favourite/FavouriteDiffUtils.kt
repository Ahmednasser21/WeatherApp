package com.ahmed.weather.iti.ui.favourite

import androidx.recyclerview.widget.DiffUtil
import com.ahmed.weather.iti.database.FavouriteDTO

class FavouriteDiffUtils:DiffUtil.ItemCallback<FavouriteDTO>() {
    override fun areItemsTheSame(oldItem: FavouriteDTO, newItem: FavouriteDTO): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: FavouriteDTO, newItem: FavouriteDTO): Boolean {
        return oldItem == newItem
    }
}