package com.ahmed.weather.iti.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.repository.IRepository
import com.ahmed.weather.iti.repository.Repository
import com.ahmed.weather.iti.ui.home.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: IRepository) : ViewModel() {

    private var _alarmList = MutableStateFlow<DataState>(DataState.Loading)
    val alarmList= _alarmList.asStateFlow()

    fun getAlarmList(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlarms()
                .catch {
                    _alarmList.value = DataState.OnFailed(it)
                }
                .collect{list->_alarmList.value = DataState.OnSuccess(list)}
        }
    }
    fun addAlarm(alarmDTO: AlarmDTO){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAlarm(alarmDTO)}
    }
    fun deleteAlarm(alarmDTO: AlarmDTO){
        viewModelScope.launch (Dispatchers.IO){
            repository.removeAlarm(alarmDTO)
        }
    }

    fun deleteExpiredAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            repository.getAllAlarms().collect { alarmList ->
                alarmList.forEach { alarm ->
                    if (alarm.timeInMillis < currentTime) {
                        repository.removeAlarm(alarm)
                        getAlarmList()
                    }
                }
            }
        }
    }

}