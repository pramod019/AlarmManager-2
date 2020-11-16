package com.example.alarmmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat


class AlarmApplication : Application() {
    companion object {
        const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        AlarmRepository.initialize(applicationContext)

        createNotificationChannel(
            CHANNEL_ID,
            "This is notification title",
            "This is notification description"
        )
    }

    private fun createNotificationChannel(id: String, title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH)
            channel.description = description
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}