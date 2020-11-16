package com.example.alarmmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.alarmmanager.model.Alarm

@Dao
interface AlarmDao {

    @Query("Select * from alarm")
    fun getAlarms(): LiveData<List<Alarm>>

    @Insert
    fun insertAlarms(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)
}