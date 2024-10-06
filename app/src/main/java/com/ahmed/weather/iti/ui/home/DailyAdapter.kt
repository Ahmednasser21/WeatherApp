package com.ahmed.weather.iti.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.databinding.DayItemBinding

class DailyAdapter : ListAdapter<DailyDTO,DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyDTO = getItem(position)
        holder.bindView(dailyDTO)
    }
    class DailyViewHolder(private val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bindView(dailyDTO: DailyDTO){
                binding.tvDay.text = dailyDTO.day
                binding.imgCondition.setImageResource(dailyDTO.img)
                binding.tvStatus.text = dailyDTO.status
                binding.tvMinMax.text = dailyDTO.minMax
            }
    }
}