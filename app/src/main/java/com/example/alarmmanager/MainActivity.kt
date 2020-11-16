package com.example.alarmmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.alarmmanager.model.Alarm
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val alarmList = arrayListOf<Alarm>()
    private val adapter = AlarmListAdapter(alarmList)
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel::class.java)

        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.add_fab)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        fab.setOnClickListener { startActivity(Intent(this, SetUpTimeActivity::class.java)) }

        alarmViewModel.getAlarms().observe(this, { alarms ->
            val now = Calendar.getInstance()
            var enabledAlarms = -1
            for ((index, alarm) in alarms.withIndex()) {

                // Checking if time is past then unchecked the switch
                if (now.timeInMillis > alarm.time) {
                    Log.d(TAG, "getAlarms: ")
                    alarm.isEnabled = false
                }
                // Checking if show alarm icon if any of the alarm is enabled
                if (alarm.isEnabled) {
                    enabledAlarms = index
                }
            }
            if (enabledAlarms != -1) {
                Log.d(TAG, "onStart: $enabledAlarms")
                SetUpTimeActivity.setupAlarmIcon(this, alarms[enabledAlarms].time)
            } else {
                SetUpTimeActivity.setupAlarmIcon(this, null)
            }
            alarmList.clear()
            alarmList.addAll(alarms)
            adapter.notifyItemRangeChanged(0, alarms.size)
        })
    }

    companion object {
        fun getFormattedTime(hours: Int, minutes: Int): String {
            val mHours = when (hours.toString().length) {
                1 -> "0$hours"
                else -> "$hours"
            }
            val mMinutes = when (minutes.toString().length) {
                1 -> "0$minutes"
                else -> "$minutes"
            }
            return "$mHours:$mMinutes"
        }
    }

    private inner class AlarmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val alarmTime: TextView = itemView.findViewById(R.id.alarm_time)
        private val alarmAmPm: TextView = itemView.findViewById(R.id.alarm_ampm)
        private val alarmDescription: TextView = itemView.findViewById(R.id.alarm_description)
        private val switchEnabled: SwitchMaterial = itemView.findViewById(R.id.switchEnabled)
        private val days: TextView = itemView.findViewById(R.id.days)
        private val deleteAlarm: ImageView = itemView.findViewById(R.id.deleteAlarm)

        fun bind(alarm: Alarm, position: Int) {
            alarmDescription.text = alarm.title
            Calendar.getInstance().apply {
                timeInMillis = alarm.time
                alarmTime.text = getFormattedTime(get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))

                days.text = SetUpTimeActivity.formatDate(timeInMillis)
                alarmAmPm.text = when {
                    get(Calendar.HOUR_OF_DAY) >= 12 -> "PM"
                    else -> "AM"
                }
            }
            val now = Calendar.getInstance()
            val future = Calendar.getInstance().apply {
                timeInMillis = alarm.time
            }
            switchEnabled.isChecked = alarm.isEnabled

            switchEnabled.setOnClickListener { v ->
                if (!(v as SwitchMaterial).isChecked) {
                    Toast.makeText(this@MainActivity, "Cancelled Alarm", Toast.LENGTH_SHORT).show()
                    WorkManager.getInstance(this@MainActivity).cancelUniqueWork(alarm.id.toString())
                    alarmViewModel.updateAlarm(Alarm(alarm.id, alarm.title, alarm.time, false))
                } else {
                    Toast.makeText(this@MainActivity, "Enabled Alarm", Toast.LENGTH_SHORT).show()
                    // If time is past then set the alarm for same time but for next day
                    if (now.timeInMillis > future.timeInMillis) {
                        future.set(Calendar.DATE, future.get(Calendar.DATE) + 1)
                        alarm.time = future.timeInMillis
                    }
                    SetUpTimeActivity.setupWork(this@MainActivity, alarm)
                    alarmViewModel.updateAlarm(Alarm(alarm.id, alarm.title, alarm.time, true))
                }
            }

            deleteAlarm.setOnClickListener {
                alarmList.removeAt(position)
                alarmViewModel.deleteAlarm(alarm)
                adapter.notifyItemRemoved(position)
            }
        }
    }

    private inner class AlarmListAdapter(private val alarms: ArrayList<Alarm>) :
        RecyclerView.Adapter<AlarmHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder {
            return AlarmHolder(layoutInflater.inflate(R.layout.alarm_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: AlarmHolder, position: Int) {
            holder.bind(alarms[position], position)
        }

        override fun getItemCount() = alarms.size
    }

}