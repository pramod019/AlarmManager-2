package com.example.alarmmanager

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

private const val TAG = "AlarmService"

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val ACTION = "action"
        const val ACTION_DISMISS = "action_dismiss"
        const val ACTION_SNOOZE = "action_snooze"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(baseContext, R.raw.alarm_samsung)
        if (intent != null && intent.extras != null) {
            val id = UUID.fromString(intent.extras?.getString("ID"))
            val title = intent.extras?.getString("TITLE", "title")
            val enabled = intent.extras?.getBoolean("ENABLED", false)
            val time = intent.extras?.getLong("TIME", 0) ?: 0

            Log.d(TAG, "onStartCommand: $id $title $time $enabled")

            val snoozeIntent = Intent(baseContext, NotificationReceiver::class.java).apply {
                Log.d(TAG, "ACTION: $id $title $time $enabled")
                putExtra(ACTION, ACTION_SNOOZE)
                putExtra("TIME", time)
                putExtra("ID", id.toString())
                putExtra("TITLE", title)
                putExtra("ENABLED", enabled)
            }
            val snoozePendingIntent =
                PendingIntent.getBroadcast(
                    baseContext,
                    0,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val snoozeAction =
                NotificationCompat.Action
                    .Builder(R.drawable.icon_close, "SNOOZE", snoozePendingIntent)
                    .build()

            val dismissIntent = Intent(baseContext, NotificationReceiver::class.java).apply {
                putExtra(ACTION, ACTION_DISMISS)
                putExtra("TIME", time)
                putExtra("ID", id.toString())
                putExtra("TITLE", title)
                putExtra("ENABLED", enabled)
            }
            val dismissPendingIntent =
                PendingIntent.getBroadcast(
                    baseContext,
                    1,
                    dismissIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val dismissAction =
                NotificationCompat.Action
                    .Builder(R.drawable.icon_close, "DISMISS", dismissPendingIntent)
                    .build()

            val contentIntent = Intent(baseContext, MainActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(baseContext, 0, contentIntent, 0)

            Calendar.getInstance().apply {
                timeInMillis = time
                val notification =
                    NotificationCompat.Builder(baseContext, AlarmApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_alarm)
                        .setContentIntent(contentPendingIntent)
                        .addAction(dismissAction)
                        .addAction(snoozeAction)
                        .setContentTitle("Alarm - $title")
                        .setContentText(
                            "Sun ${
                                MainActivity.getFormattedTime(
                                    get(Calendar.HOUR_OF_DAY),
                                    get(Calendar.MINUTE)
                                )
                            }"
                        )
                        .build()

                startForeground(2, notification)
                mediaPlayer?.start()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}