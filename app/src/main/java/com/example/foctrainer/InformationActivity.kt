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
    private lateinit var tvDatePicked: TextView

    //hardcoding this part for now
    val workoutDate = "22-03-2022 10:00"

    //val etSets = findViewById<EditText>(R.id.etSets)
    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private lateinit var binding: ActivityInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvDatePicked = findViewById(R.id.tvPickedDate)
        tvDatePicked.setText(workoutDate)
    }

    fun addWorkoutClick(view: View){
        val etNotes = findViewById<EditText>(R.id.etNotes)
        val workoutSets = 2
        val workoutNotes = etNotes.text.toString()
        //val workoutDate = tvDatePicked.text.toString()

        val date = ScheduleModel(5, 2, 1, workoutDate, workoutNotes,  workoutSets, "Push Ups")
        scheduleViewModel.createNewDate(date = date)

        val intent = Intent(this, ExerciseActivity::class.java)
        startActivity(intent)
    }

    // Insert etSets + etNotes in ScheduleTable
    // Export tvDatePicked from ??? <-- need to access
}