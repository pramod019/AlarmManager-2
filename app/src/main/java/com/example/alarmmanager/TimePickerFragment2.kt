package com.example.alarmmanager

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "arg_time"

class TimePickerFragment2() : DialogFragment() {
    interface Callbacks {
        fun onTimeSet(mtime: Date)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val timeChangeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            callbacks?.onTimeSet(calendar.time)
        }

        val calendar = Calendar.getInstance().apply {
            time = arguments?.getSerializable(ARG_TIME) as Date
        }

        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeChangeListener,
            initialHour,
            initialMinute,
            false
        )
    }

    companion object {
        fun newInstance(date: Date): TimePickerFragment2 {
            return TimePickerFragment2().apply {
                val args = Bundle().apply {
                    putSerializable(ARG_TIME, date)
                }
                arguments = args
            }

        }
    }
}