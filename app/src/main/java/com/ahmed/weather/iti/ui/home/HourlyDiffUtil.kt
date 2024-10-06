package com.ahmed.weather.iti.ui.home

import androidx.recyclerview.widget.DiffUtil

class HourlyDiffUtil: DiffUtil.ItemCallback<HourlyDTO>() {
    override fun areItemsTheSame(oldItem: HourlyDTO, newItem: HourlyDTO): Boolean {
            return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: HourlyDTO, newItem: HourlyDTO): Boolean {
        return oldItem == newItem
    }
}