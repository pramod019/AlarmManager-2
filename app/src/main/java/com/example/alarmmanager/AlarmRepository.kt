package com.example.alarmmanager

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.alarmmanager.database.AlarmDao
import com.example.alarmmanager.database.AlarmDatabase
import com.example.alarmmanager.model.Alarm
import java.lang.IllegalStateException
import java.util.concurrent.Executors

private const val DATABASE_NAME = "alarm-database"

private const val TAG = "AlarmRepository"

class AlarmRepository private constructor(context: Context) {
    private val database: AlarmDatabase =
        Room.databaseBuilder(context.applicationContext, AlarmDatabase::class.java, DATABASE_NAME)
            .build()

    private val alarmDao = database.alarmDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getAlarms() = alarmDao.getAlarms()

    fun insertAlarms(alarm: Alarm) {
        executor.execute {
            alarmDao.insertAlarms(alarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        Log.d(TAG, "updateAlarm: $alarm")
        executor.execute {
            alarmDao.updateAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        Log.d(TAG, "deleteAlarm: $alarm")
        executor.execute {
            alarmDao.deleteAlarm(alarm)
        }
    }

    companion object {
        private var INSTANCE: AlarmRepository? = null

        fun initialize(context: Context) {
            INSTANCE = AlarmRepository(context)
        }

        fun get(): AlarmRepository {
            return INSTANCE ?: throw IllegalStateException()
        }
    }
}