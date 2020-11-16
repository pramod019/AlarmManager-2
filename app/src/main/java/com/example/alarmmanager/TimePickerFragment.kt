package com.example.alarmmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class TimePickerFragment : Fragment() {
    private lateinit var hoursViewPager: ViewPager2
    private lateinit var minutesViewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_picker, container, false)

        hoursViewPager = view.findViewById(R.id.hoursViewPager)
        minutesViewPager = view.findViewById(R.id.minutesViewPager)

        hoursViewPager.adapter = TimeViewPager(this)
        minutesViewPager.adapter = TimeViewPager(this)

        hoursViewPager.offscreenPageLimit = 3
        minutesViewPager.offscreenPageLimit = 3

        return view
    }

    private inner class TimeViewPager(private val fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount() = 10

        override fun createFragment(position: Int) = NumberFragment()
    }
}