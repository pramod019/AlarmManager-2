package com.example.alarmmanager.database

import androidx.room.TypeConverter
import java.util.*

class AlarmTypeConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}