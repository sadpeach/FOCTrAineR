package com.example.foctrainer.schedule

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

import java.util.*

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.MainActivity
import com.example.foctrainer.R
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.utils.CurrentDayDecorator
import com.example.foctrainer.utils.EventDecorator
import com.example.foctrainer.viewModel.ScheduleExerciseViewModel
import com.example.foctrainer.viewModel.ScheduleExerciseViewModelFactory
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.text.SimpleDateFormat
import kotlin.collections.HashSet


class ScheduleCalendar : AppCompatActivity() {
    private var userId :Int = 0
    companion object{
        val TAG = "Schedule"
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var selectedDate :String = getTodayDate()
        val color: Int = Color.parseColor("#FF0000")
        private val PREFS_NAME = "SharedPreferenceFile"


        private fun getTodayDate() : String{
            val calendarDate = Calendar.getInstance().time
            val currentDate = sdf.format(calendarDate)
            return currentDate
        }
    }

    private lateinit var adapter: ScheduleCalendarAdapter

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private val scheduleExerciseViewModel: ScheduleExerciseViewModel by viewModels {
        ScheduleExerciseViewModelFactory((application as FocTrainerApplication).scheduleExerciseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_calendar)

        val settings = getSharedPreferences(PREFS_NAME, 0)
        userId = settings.getInt("userId",0)

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

        val intent = Intent(this, CreateScheduleActivity::class.java)
        intent.putExtra("selectedDate", selectedDate)
        startActivity(intent)

    }

    private fun calendar(){

        val calendarView = findViewById<MaterialCalendarView>(R.id.datePicker)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate = CalendarDay.from(year,  month, day)
        calendarView.addDecorators(CurrentDayDecorator(this, currentDate))
        showEventDot(calendarView)

        calendarView.setOnDateChangedListener(
            OnDateSelectedListener() { widget:MaterialCalendarView,date:CalendarDay,selected:Boolean ->

                selectedDate = sdf.format(date.date)
                changeEventViewBySelectedDate(selectedDate)
        })

    }
    private fun showEventDot(calendarView:MaterialCalendarView){
        scheduleViewModel.getDates(userId = userId ).observe(this) { allDates ->

            val eventDates = HashSet <CalendarDay>()
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
            for (date in allDates){
                    val dateC : CalendarDay = CalendarDay.from(dateFormatter.parse(date))
                    eventDates.add(dateC)
                }
            calendarView.addDecorators(EventDecorator(color = color, eventDates))
        }

    }

    private fun changeEventViewBySelectedDate(selectedDate:String){

//        scheduleViewModel.getScheduleByDate(selectedDate,userId).observe(this) { schedules ->
//            schedules.let { adapter.submitList(it) }
//        }

        scheduleExerciseViewModel.getScheduleExercise(selectedDate,userId).observe(this) { schedules ->
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