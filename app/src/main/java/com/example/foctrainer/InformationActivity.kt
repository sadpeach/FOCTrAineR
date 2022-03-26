package com.example.foctrainer

import android.app.Activity
import android.content.ContentProvider
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityInformationBinding
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.schedule.ScheduleCalendar
import com.example.foctrainer.schedule.ScheduleCalendarAdapter
import com.example.foctrainer.viewModel.ExerciseViewModel
import com.example.foctrainer.viewModel.ExerciseViewModelFactory
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory
import com.github.mikephil.charting.data.Entry
import java.util.ArrayList
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView


class InformationActivity : AppCompatActivity() {
    private lateinit var tvDatePicked: TextView
    private lateinit var adapter: ExerciseAdapter
    //private lateinit var etNotes: EditText
    //private lateinit var etSets: EditText
    //private lateinit var etName: EditText
    var exerciseId = -1
    private val exList = ArrayList<ExerciseModel>()
    //val etSets = findViewById<EditText>(R.id.etSets)

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    private lateinit var binding: ActivityInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        menu()

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

        val it = intent
        val selectedDate = it.getStringExtra("selectedDate")

        tvDatePicked = findViewById(R.id.tvPickedDate)

        tvDatePicked.setText(selectedDate)

    }

    fun addWorkoutClick(view: View){
        val workoutSets = binding.etSets.text.toString().toInt()
        val workoutNotes = binding.etNotes.text.toString()
        val workoutDate = tvDatePicked.text.toString()
        val title = binding.etName.text.toString()

        val date = ScheduleModel( userId = 1,exerciseId = exerciseId, startDateTime= workoutDate,notes= workoutNotes, no_of_sets= workoutSets,title= title)
        scheduleViewModel.createNewDate(date = date)

        val intent = Intent(this, ScheduleCalendar::class.java)
        startActivity(intent)
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

    // Insert etSets + etNotes in ScheduleTable
    // Export tvDatePicked from ??? <-- need to access
}