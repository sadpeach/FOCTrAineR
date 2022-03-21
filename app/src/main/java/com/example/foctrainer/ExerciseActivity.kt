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
import com.example.foctrainer.databinding.ActivityExerciseBinding
import com.github.mikephil.charting.data.Entry

class ExerciseActivity : AppCompatActivity() {
    var title = ""
    var noSet = 0
    var date = ""
    var userID = 0
    var notes = ""

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>? = null


    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise2)
        //val exercise = ExerciseModel(1, "Push ups", 1)
        //exerciseViewModel.createNewExercise(exercise = exercise)



        layoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSchedule)
        recyclerView.layoutManager = layoutManager

        //val date = ScheduleModel(1, 5, 1, startDateTime, "", 5, "")
        //scheduleViewModel.createNewDate(date = date)

        val scheduleList = ArrayList<String>()
        scheduleViewModel.allDates.observe(this, Observer<List<ScheduleModel>>(){
            schedule ->
            for (i in schedule){
                userID = i.userId
                var exerciseid = i.exerciseId
                date = i.startDateTime
                notes = i.notes.toString()
                noSet = i.no_of_sets
                title = i.title

                scheduleList.add("$userID")
                scheduleList.add("$exerciseid")
                scheduleList.add("$date")
                scheduleList.add("$notes")
                scheduleList.add("$noSet")
                scheduleList.add("$title")
            }
        })
       // adapter = ScheduleRecyclerAdapter(scheduleList)
        
        
        val recyclerViewSchedule = findViewById<RecyclerView>(R.id.recyclerViewSchedule)
        recyclerViewSchedule.adapter = adapter
        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(this@ExerciseActivity)
            adapter = ScheduleRecyclerAdapter(scheduleList)
        }

        val it = intent
        val startDateTime = it.getStringExtra("startDateTime")

        displayExerciseList()
    }

    fun displayExerciseList(){
        val tvExerciseName = findViewById<TextView>(R.id.itemTitle)
        val tvNoSets = findViewById<TextView>(R.id.no_of_sets)
        val tvDate = findViewById<TextView>(R.id.dateText)
        val tvNotes = findViewById<TextView>(R.id.tvNotes)

        tvExerciseName?.text = ""
        tvNoSets?.text = ""
        tvDate?.text = ""
        tvNotes?.text = ""
/*
        exerciseViewModel.allExercise.observe(this, Observer<List<ExerciseModel>>(){
            exercises ->
            tvExerciseName.append(exercises[1].name)
        })
*/
        // 1. Insert startDateTime + no_of_sets + notes in ScheduleModel + title?
        // 2. Insert exerciseName in ExercieModel/title in ScheduleModel
        // 3. Read exerciseName from exerciseModel alt. title from ScheduleModel
        // 4. Read notes + startDateTime + no_of_sets from ScheduleModel
        // 5.
        scheduleViewModel.allDates.observe(this, Observer<List<ScheduleModel>>(){
            entries ->
            tvExerciseName.append(entries[0].title)
           // tvExerciseName.text = entries[1].title
            tvDate.append(entries[0].startDateTime)
           // tvDate.text = entries[1].startDateTime
            tvNoSets.append(entries[0].no_of_sets.toString())
           // tvNotes.text = entries[1].notes
            tvNotes.setText(entries[0].notes)
           // tvNoSets.text = entries[1].no_of_sets.toString()
            Log.d("hello", entries[0].toString())
        })
    }
}


