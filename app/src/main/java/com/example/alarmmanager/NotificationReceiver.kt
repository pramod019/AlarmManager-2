package com.example.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.alarmmanager.model.Alarm
import java.util.*

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val repository = AlarmRepository.get()
        if (intent != null && intent.extras != null) {
            val id = UUID.fromString(intent.extras?.getString("ID"))
            val title = intent.extras?.getString("TITLE", "title") ?: "Default Title"
            val enabled = intent.extras?.getBoolean("ENABLED", false) ?: false
            val mTime = intent.extras?.getLong("TIME", 0) ?: 0

            Log.d(TAG, "onReceive: $id $title $mTime $enabled")
            when (intent.extras?.getString(AlarmService.ACTION)) {
                AlarmService.ACTION_DISMISS -> {
                    Toast.makeText(context, "Alarm Stopped", Toast.LENGTH_SHORT).show()
                    repository.updateAlarm(Alarm(id, title, mTime, false))
                    context.stopService(Intent(context, AlarmService::class.java))
                }
                AlarmService.ACTION_SNOOZE -> {
                    context.stopService(Intent(context, AlarmService::class.java))
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = mTime
                        set(Calendar.MINUTE, get(Calendar.MINUTE) + 2)
                        Log.d(
                            TAG,
                            "onReceive: ${
                                MainActivity.getFormattedTime(
                                    get(Calendar.HOUR_OF_DAY),
                                    get(Calendar.MINUTE)
                                )
                            }"
                        )
                    }
                    SetUpTimeActivity.setupWork(
                        context,
                        Alarm(id, title, cal.timeInMillis, enabled),
                    )
                    // Update the alarm time by adding snooze minutes
                    repository.updateAlarm(Alarm(id, title, cal.timeInMillis, true))
                    Toast.makeText(context, "Alarm Snooze will be in 2 min", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}