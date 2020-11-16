package com.example.alarmmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class SetAlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        val fragment = supportFragmentManager.findFragmentById(R.id.timePickerFragment)

        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.timePickerFragment, TimePickerFragment())
                .commit()
        }
    }
}