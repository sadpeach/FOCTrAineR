package com.example.foctrainer.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import com.google.android.material.bottomnavigation.BottomNavigationView

import java.util.*
import android.widget.Toast

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.MainActivity
import com.example.foctrainer.R
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.time.Year


class ScheduleCalendar : AppCompatActivity() {

    companion object{
        val TAG = "Schedule"
        var selectedDate :String = getTodayDate()

        private fun getTodayDate() : String{
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val calendarDate = Calendar.getInstance().time
            Log.d(TAG,"CalendarDate:$calendarDate")
            val currentDate = sdf.format(calendarDate)
            Log.d(TAG,"today's date is -> $currentDate")
            return currentDate
        }
    }

    private lateinit var adapter: ScheduleCalendarAdapter

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_calendar)
        menu()
        calendar()

        //recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.datePickerEvents)
        adapter = ScheduleCalendarAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        changeEventViewBySelectedDate(selectedDate)

    }

    //TODO: when clicking on button direct to form for user schedule input
    fun addButtonSetOnClick(view: View){

        Log.d(TAG,"selectedDate: $selectedDate")

    }

    private fun calendar(){

        val calendarView = findViewById<CalendarView>(R.id.datePicker)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val calendar = Calendar.getInstance()
            calendar[year, month] = dayOfMonth
            val selectedDate = sdf.format(calendar.time)
            changeEventViewBySelectedDate(selectedDate)

        }
    }

    private fun changeEventViewBySelectedDate(selectedDate:String){


        scheduleViewModel.getScheduleByDate(selectedDate).observe(this) { schedules ->
            schedules.let { adapter.submitList(it) }
        }
    }

    private fun menu(){

        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.schedule)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true

                }
                R.id.schedule -> {
                    true
                }

                else -> false
            }
        }
    }
}