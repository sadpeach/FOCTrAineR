package com.example.foctrainer

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityPlannerBinding
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.viewModel.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.example.foctrainer.entity.ExerciseModel

class PlannerActivity : AppCompatActivity() {
    private lateinit var tvDatePicker: TextView
    private lateinit var btnDatePicker: Button
    private lateinit var btnSchedule: Button
    private lateinit var binding: ActivityPlannerBinding



    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as FocTrainerApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        tvDatePicker = findViewById(R.id.tvDate)
        btnDatePicker = findViewById(R.id.btnDatePicker)
        btnSchedule = findViewById(R.id.btnSchedule)
        btnSchedule.setEnabled(false)
        val dateTime: Calendar
        dateTime = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dateTime.set(Calendar.YEAR, year)
            dateTime.set(Calendar.MONTH, month)
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(dateTime)
        }


        btnDatePicker.setOnClickListener {
            DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR), dateTime.get(
                Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show()

            btnSchedule.setEnabled(true)
        }
        val startDateTime = DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR), dateTime.get(
            Calendar.MONTH),
            dateTime.get(Calendar.DAY_OF_MONTH)).toString()

        val user2 = UserModel(5, "hannah", "abc", 175f, 70f, 22.86f)
        userViewModel.createNewUser(user = user2)

        val date = ScheduleModel(1, 5, 1, startDateTime, "", 5, "")
        scheduleViewModel.createNewDate(date = date)

        btnSchedule.setOnClickListener {
            val myIntent = Intent(this, ExerciseActivity::class.java)
            myIntent.putExtra("startDateTime", startDateTime)
            startActivity(myIntent)
        }

    }

    //fun onButtonClick(view: View){
    //    val myIntent = Intent(this, ExerciseActivity::class.java)
    //    myIntent.putExtra("startDateTime", startDateTime)
    //    startActivity(myIntent)
    //}

    private fun updateLabel(myCalendar: Calendar){
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tvDatePicker.setText(sdf.format(myCalendar.time))

    }
}