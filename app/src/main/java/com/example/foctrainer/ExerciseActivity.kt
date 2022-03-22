package com.example.foctrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.viewModel.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry

class ExerciseActivity : AppCompatActivity() {

    companion object{
        val TAG = "ExerciseActivity"
    }

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise2)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSchedule)
        val adapter = ScheduleRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        scheduleViewModel.allDates.observe(this) { schedules ->
            schedules.let { adapter.submitList(it) }
        }

    }}


