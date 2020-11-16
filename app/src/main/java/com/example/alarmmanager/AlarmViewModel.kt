package com.example.alarmmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.alarmmanager.model.Alarm

class AlarmViewModel : ViewModel() {
    private val database = AlarmRepository.get()

    fun getAlarms(): LiveData<List<Alarm>> {
        return database.getAlarms()
    }

    fun updateAlarm(alarm: Alarm) {
        return database.updateAlarm(alarm)
    }

    fun insertAlarm(alarm: Alarm) {
        return database.insertAlarms(alarm)
    }

    fun deleteAlarm(alarm: Alarm) {
        return database.deleteAlarm(alarm)
    }
}