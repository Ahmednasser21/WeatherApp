package com.ahmed.weather.iti.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.databinding.HourItemBinding

class HourlyAdapter :ListAdapter<HourlyDTO,HourlyAdapter.HourlyDataViewHolder>(HourlyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyDataViewHolder {
        val binding = HourItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyDataViewHolder, position: Int) {
        val hourlyData = getItem(position)
        holder.bindView(hourlyData)
    }

    class HourlyDataViewHolder(private val binding: HourItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bindView(hourlyData: HourlyDTO) {
            binding.tvTime.text = hourlyData.time
            binding.imgDuration.setImageResource(hourlyData.image)
            binding.tvTemp.text = hourlyData.temp
        }
    }
}