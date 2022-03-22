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
import com.example.foctrainer.R

class ExerciseActivity : AppCompatActivity() {


    companion object {
        val TAG = "ExerciseActivity"
    }

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise2)

        var layoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSchedule)
        val adapter = ScheduleRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateText = findViewById<TextView>(R.id.dateText)
        //dateText.setText("22-03-2022 10:00")

        scheduleViewModel.allDates.observe(this) { schedules ->
            schedules.let { adapter.submitList(it) }
            //     dateText.append(schedules[0].startDateTime)

        }


    }

//    private fun displayExerciseList() {
//        val tvTitle = findViewById<TextView>(R.id.itemTitle)
//        val tvNoSets = findViewById<TextView>(R.id.no_of_sets)
//        val tvDate = findViewById<TextView>(R.id.dateText)
//        val tvNotes = findViewById<TextView>(R.id.tvNotes)
//
//        tvTitle!!.text = ""
//        tvNoSets!!.text = ""
//        tvDate!!.text = ""
//        tvNotes!!.text = ""
//
//        scheduleViewModel.allDates.observe(this, Observer<List<ScheduleModel>>() { entries ->
//            tvTitle.append(entries[0].title)
//            tvDate.append(entries[0].startDateTime)
//            tvNoSets.append(entries[0].no_of_sets.toString())
//            tvNotes.setText(entries[0].notes)
//            Log.d("hello", entries[0].toString())
//        })
//    }
}


