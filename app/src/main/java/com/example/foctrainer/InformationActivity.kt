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
import com.example.foctrainer.schedule.ScheduleCalendar
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory

class InformationActivity : AppCompatActivity() {
    private lateinit var tvDatePicked: TextView
    //private lateinit var etNotes: EditText
    //private lateinit var etSets: EditText
    //private lateinit var etName: EditText

    //hardcoding this part for now
    //val workoutDate = "22-03-2022 10:00"

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

        val it = intent
        val selectedDate = it.getStringExtra("selectedDate")

        tvDatePicked.setText(selectedDate)
    }

    fun addWorkoutClick(view: View){
        val workoutSets = binding.etSets.text.toString().toInt()
        val workoutNotes = binding.etNotes.text.toString()
        val workoutDate = tvDatePicked.text.toString()
        val title = binding.etName.text.toString()

        val date = ScheduleModel(0, 1, 1, workoutDate, workoutNotes,  workoutSets, title)
        scheduleViewModel.createNewDate(date = date)

        val intent = Intent(this, ScheduleCalendar::class.java)
        startActivity(intent)
    }

    // Insert etSets + etNotes in ScheduleTable
    // Export tvDatePicked from ??? <-- need to access
}