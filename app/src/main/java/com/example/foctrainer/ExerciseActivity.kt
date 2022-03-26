package com.example.foctrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.schedule.ScheduleCalendarAdapter
import com.example.foctrainer.viewModel.*
import java.util.ArrayList
import androidx.lifecycle.Observer

class ExerciseActivity : AppCompatActivity() {

    private lateinit var adapter: ExerciseAdapter
    lateinit var exNameList : ArrayList<String>
    var exerciseId = -1

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        val catlist = ArrayList<ExerciseModel>()
        exerciseViewModel.allExercise.observe(this, Observer<List<ExerciseModel>>(){ exercise->
            for (ex in exercise){
                exerciseId = ex.id
                catlist.add(ex)
            }})

        val recyclerView = findViewById<RecyclerView>(R.id.exerciseList)
        adapter = ExerciseAdapter(catlist)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

}


