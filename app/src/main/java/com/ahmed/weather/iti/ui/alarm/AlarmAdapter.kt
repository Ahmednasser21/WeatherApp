package com.ahmed.weather.iti.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.databinding.AlarmItemBinding

class AlarmAdapter (private val onDeleteAlarmListener: OnDeleteAlarmListener) :ListAdapter<AlarmDTO,AlarmAdapter.AlarmViewHolder>(AlarmDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmDTO = getItem(position)
        holder.bindView(alarmDTO, onDeleteAlarmListener)
    }

    class AlarmViewHolder(private val binding: AlarmItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bindView(alarmDTO: AlarmDTO,onDeleteAlarmListener: OnDeleteAlarmListener){
            binding.tvAlarmTime.text = alarmDTO.formattedTime
            binding.btnRemove.setOnClickListener{
                onDeleteAlarmListener.onClick(alarmDTO)
            }
        }
    }
}