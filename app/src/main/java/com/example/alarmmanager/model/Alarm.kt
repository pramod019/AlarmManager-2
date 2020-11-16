package com.example.alarmmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Alarm(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val title: String = "Default Title",
    var time: Long = 5,
    var isEnabled: Boolean = false
)