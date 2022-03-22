package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityInformationBinding
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory

class InformationActivity : AppCompatActivity() {
    //val etSets = findViewById<EditText>(R.id.etSets)


    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private lateinit var binding: ActivityInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun addWorkoutClick(view: View){
        val etNotes = findViewById<EditText>(R.id.etNotes)
        val tvDatePicked = findViewById<TextView>(R.id.tvPickedDate)
        val workoutSets = 2
        val workoutNotes = etNotes.text.toString()
        val workoutDate = tvDatePicked.text.toString()

        val date = ScheduleModel(1, 5, 1, workoutDate, workoutNotes, workoutSets, "Push Ups")
        scheduleViewModel.createNewDate(date = date)

        val intent = Intent(this, PlannerActivity::class.java)
        startActivity(intent)
    }

    // Insert etSets + etNotes in ScheduleTable
    // Export tvDatePicked from ??? <-- need to access
}