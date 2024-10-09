package com.ahmed.weather.iti.ui.alarm

import com.ahmed.weather.iti.database.AlarmDTO

interface OnDeleteAlarmListener {
    fun onClick(alarmDTO: AlarmDTO)
}