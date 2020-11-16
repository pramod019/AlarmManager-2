package com.example.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModelProviders
import androidx.work.*
import com.example.alarmmanager.model.Alarm
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG = "SetUpTimeActivity"

class SetUpTimeActivity : AppCompatActivity(), TimePickerFragment2.Callbacks {

    private lateinit var labelInput: TextInputLayout
    private lateinit var timeInput: TextInputLayout
    private lateinit var calendar: CalendarView
    private lateinit var saveButton: Button
    private lateinit var alarmViewModel: AlarmViewModel
    private val selectedDateAndTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_time)

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel::class.java)
        labelInput = findViewById(R.id.labelInput)
        timeInput = findViewById(R.id.timeInput)
        saveButton = findViewById(R.id.save_button)
        calendar = findViewById(R.id.calendar)
    }

    override fun onStart() {
        super.onStart()

        labelInput.requestFocus()
        timeInput.setEndIconOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerFragment2.newInstance(calendar.time)
            timePicker.show(supportFragmentManager, "Time Picker")
        }

        val cal = Calendar.getInstance()
        val maxCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
        }

        calendar.minDate = cal.timeInMillis
        calendar.maxDate = maxCal.timeInMillis
        calendar.date = cal.timeInMillis

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDateAndTime.set(Calendar.MONTH, month)
            selectedDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDateAndTime.set(Calendar.YEAR, year)
        }

        saveButton.setOnClickListener {
            if (labelInput.editText != null && timeInput.editText != null) {
                if (labelInput.editText!!.text.isNotEmpty() && timeInput.editText!!.text.isNotEmpty()) {
                    setAlarm()
                } else {
                    Toast.makeText(this, "All Fields are Required!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setAlarm() {
        val alarm = Alarm(
            title = labelInput.editText!!.text.toString(),
            isEnabled = true,
            time = selectedDateAndTime.timeInMillis
        )
        alarmViewModel.insertAlarm(alarm)
        Calendar
            .getInstance().apply {
                timeInMillis = alarm.time
                setupWork(
                    this@SetUpTimeActivity,
                    alarm,
                )
            }
        finish()
    }

    companion object {
        private fun createDataFromAlarm(alarm: Alarm): Data {
            val builder = Data.Builder()
            builder.putString("ID", alarm.id.toString())
            builder.putLong("TIME", alarm.time)
            builder.putString("TITLE", alarm.title)
            builder.putBoolean("ENABLED", alarm.isEnabled)
            return builder.build()
        }

        fun setupWork(context: Context, alarm: Alarm) {
            val constraints = Constraints
                .Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val now = Calendar.getInstance()
            val diff = alarm.time - now.timeInMillis
            val seconds = diff / 1000

            Calendar.getInstance().apply {
                set(Calendar.SECOND, seconds.toInt())
                Log.d(
                    TAG,
                    "setupWork: ${formatDate(timeInMillis)} ${get(Calendar.HOUR_OF_DAY)} ${
                        get(Calendar.MINUTE)
                    }"
                )
            }

            val workRequest = OneTimeWorkRequestBuilder<AlarmWork>()
                // Calculate Initial Delay
                .setInitialDelay(seconds, TimeUnit.SECONDS)
                .setInputData(createDataFromAlarm(alarm))
                .setConstraints(constraints)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(alarm.id.toString(), ExistingWorkPolicy.REPLACE, workRequest)

            setupAlarmIcon(context, alarm.time)
        }

         fun setupAlarmIcon(context: Context, time: Long?) {
            val pi = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (time == null) {
                alarmManager.cancel(pi)
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val info = AlarmManager.AlarmClockInfo(time, pi)
                    alarmManager.setAlarmClock(info, pi)
                }
            }
        }

        fun formatDate(time: Long): String {
            Calendar.getInstance().apply {
                timeInMillis = time
                val day = get(Calendar.DATE)
                val month = get(Calendar.MONTH)
                val year = get(Calendar.YEAR)
                return "$day-$month-$year"
            }
        }
    }


    override fun onTimeSet(mtime: Date) {

        Calendar.getInstance().apply {
            time = mtime

            selectedDateAndTime.set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
            selectedDateAndTime.set(Calendar.MINUTE, get(Calendar.MINUTE))

            val alarmAmPm = when {
                get(Calendar.HOUR_OF_DAY) >= 12 -> "PM"
                else -> "AM"
            }
            timeInput.editText?.setText(
                MainActivity.getFormattedTime(
                    get(Calendar.HOUR_OF_DAY),
                    get(Calendar.MINUTE)
                ) + " " + alarmAmPm
            )
        }
    }
}