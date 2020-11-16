package com.example.alarmmanager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "AlarmWork"

class AlarmWork(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val id = inputData.getString("ID")
        val time = inputData.getLong("TIME", 0)
        val enabled = inputData.getBoolean("ENABLED", false)
        val title = inputData.getString("TITLE")
        Log.d(TAG, "doWork: $id $title $time $enabled")
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ID", id)
            putExtra("TIME", time)
            putExtra("TITLE", title)
            putExtra("ENABLED", enabled)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        return Result.success()
    }
}