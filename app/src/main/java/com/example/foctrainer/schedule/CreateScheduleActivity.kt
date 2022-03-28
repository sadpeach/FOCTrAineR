package com.example.foctrainer.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityCreateScheduleBinding
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.viewModel.ExerciseViewModel
import com.example.foctrainer.viewModel.ExerciseViewModelFactory
import com.example.foctrainer.viewModel.ScheduleViewModel
import com.example.foctrainer.viewModel.ScheduleViewModelFactory
import java.util.*
import android.content.Intent
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter as ArrayAdapter1


class CreateScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateScheduleBinding
    private lateinit var selectedDate:String
    private lateinit var adapter : ArrayAdapter1<DropDownDisplay>
    private var selectedExerciseId:Int  = 0
    private var userId:Int = 0
    private var scheduleId: Int = 0

    companion object {
        const val TAG = "CreateScheduleActivity"
        private val PREFS_NAME = "SharedPreferenceFile"
    }

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settings = getSharedPreferences(PREFS_NAME, 0)
        userId = settings.getInt("userId", 0)

        selectedDate = intent.getStringExtra("selectedDate").toString()
        scheduleId = intent.getIntExtra("scheduleId",0)
        if (scheduleId > 0){
            Log.d(TAG,"retrieving scheduleId: $scheduleId")
            getScheduledExercise()
        }

        loadExercises()

        (binding.exerciseDropDown.editText as AutoCompleteTextView).onItemClickListener =
            OnItemClickListener { adapterView, view, position, id ->
                val selectedExercise: DropDownDisplay? = adapter.getItem(position)
                if (selectedExercise != null) {
                    selectedExerciseId = selectedExercise.getId()
                    Log.d(TAG,"User Selected:"+selectedExercise.getDropDownText()+", id=${selectedExercise.getId()}")
                }
            }

        binding.submitButton.setOnClickListener(){

            var eventTitle = binding.eventTitle.text.toString()
            var eventGoal = binding.eventGoal.text.toString().toInt()
            var eventNote = binding.eventNote.text.toString()

            Log.d(TAG,"adding userId=$userId, exerciseId=$selectedExerciseId, startDateTime=$selectedDate, notes=$eventNote, no_of_sets=$eventGoal, title=$eventTitle into database")

            val date = ScheduleModel( userId = userId,exerciseId = selectedExerciseId, startDateTime= selectedDate,notes= eventNote, no_of_sets=eventGoal,title= eventTitle)
            scheduleViewModel.createNewDate(date = date)

            Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ScheduleCalendar::class.java)
            startActivity(intent)
        }


        //create channel for notification
//        NotificationHelper.createNotificationChannel(this,
//            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
//            getString(R.string.app_name), "App notification channel.")
//
//        binding.setNotification.setOnCheckedChangeListener { buttonView, isChecked
//            // Responds to switch being checked/unchecked
//
//
//
//        }
//
//
//        binding.saveScheduleButton.setOnClickListener(){
//
//        }


    }
    private fun getScheduledExercise(){
        scheduleViewModel.getScheduledExerciseById(scheduleId).observe( this,{schedule ->

            binding.eventTitle.setText(schedule.title)
            binding.eventNote.setText(schedule.notes)
//            binding.eventGoal.setText("hey")
            binding.exerciseAutoCompleteTextView.setText(schedule.exerciseId)

        })
    }

    private fun loadExercises(){

        exerciseViewModel.allExercise.observe( this,{allExercises ->

            val exercises: ArrayList<DropDownDisplay> = ArrayList()
            for (exercise in allExercises){
                exercises.add(DropDownDisplay(exercise.name, exercise.id))
            }

            adapter = ArrayAdapter1(this, com.example.foctrainer.R.layout.list_item, exercises)

            val exerciseDropDown: AutoCompleteTextView = binding.exerciseAutoCompleteTextView
            exerciseDropDown.setAdapter(adapter)
    })
    }

    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}

