package com.example.alarmmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alarmmanager.model.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@TypeConverters(AlarmTypeConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}