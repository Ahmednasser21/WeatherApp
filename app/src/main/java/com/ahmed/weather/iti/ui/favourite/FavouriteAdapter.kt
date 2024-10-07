package com.ahmed.weather.iti.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.databinding.FavItemBinding

class FavouriteAdapter(private val onDeleteClickListener: OnDeleteClickListener) :
    ListAdapter<FavouriteDTO, FavouriteAdapter.FavouriteViewHolder>(FavouriteDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {

        val binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favouriteDTO = getItem(position)
        holder.bindView(favouriteDTO,onDeleteClickListener)
    }

    class FavouriteViewHolder(private val binding: FavItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(favouriteDTO: FavouriteDTO,onDeleteClickListener: OnDeleteClickListener) {
            binding.tvFavCityName.text = favouriteDTO.cityName
            binding.btnRemove.setOnClickListener{
                onDeleteClickListener.onClick(favouriteDTO)
            }
        }

    }

}