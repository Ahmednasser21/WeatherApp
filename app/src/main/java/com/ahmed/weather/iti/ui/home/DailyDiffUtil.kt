package com.ahmed.weather.iti.ui.home

import androidx.recyclerview.widget.DiffUtil

class DailyDiffUtil:DiffUtil.ItemCallback<DailyDTO>() {
    override fun areItemsTheSame(oldItem: DailyDTO, newItem: DailyDTO): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: DailyDTO, newItem: DailyDTO): Boolean {
        return oldItem == newItem
    }
}