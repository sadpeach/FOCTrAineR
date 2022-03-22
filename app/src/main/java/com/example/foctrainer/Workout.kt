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

class Workout : AppCompatActivity() {

    companion object{
        val TAG = "WorkoutActivity"
    }

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val it = intent
        val date = it.getStringExtra("startDateTime")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        val adapter = WorkoutRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewModel.allExercise.observe(this) { exercises ->
            exercises.let {adapter.submitList(it)}

        }

    }
}