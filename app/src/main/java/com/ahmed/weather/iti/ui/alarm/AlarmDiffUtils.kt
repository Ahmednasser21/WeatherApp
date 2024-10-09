package com.ahmed.weather.iti.ui.alarm

import androidx.recyclerview.widget.DiffUtil
import com.ahmed.weather.iti.database.AlarmDTO

class AlarmDiffUtils:DiffUtil.ItemCallback<AlarmDTO>() {
    override fun areItemsTheSame(oldItem: AlarmDTO, newItem: AlarmDTO): Boolean {
       return oldItem.timeInMillis == newItem.timeInMillis
    }

    override fun areContentsTheSame(oldItem: AlarmDTO, newItem: AlarmDTO): Boolean {
     return oldItem == newItem
    }
}