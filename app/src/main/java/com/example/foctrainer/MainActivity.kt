package com.example.foctrainer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityMainBinding
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.schedule.ScheduleCalendar
import com.example.foctrainer.viewModel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainRecyclerAdapter

    //BarChart
    private lateinit var myBarChart: BarChart

//    val homeFragment = HomeFragment()
//    val scheduleFragment = ScheduleFragment()

    lateinit var  caloriesList: ArrayList<BarEntry>
    lateinit var exNameList : ArrayList<String>
    var userID = -1
    var exerciseId = -1

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as FocTrainerApplication).userRepository)
    }

    private val completeExerciseModel: CompletedExerciseViewModel by viewModels {
        CompletedExerciseViewModelFactory((application as FocTrainerApplication).completedExerciseRepository)
    }

    private val PREFS_UserID = "SharedPreferenceFile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settings = getSharedPreferences(PREFS_UserID, 0)
        userID = settings.getInt("userId", -1)

        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = MainRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseViewModel.allExercise.observe(this) { exercise ->
            exercise.let { adapter.submitList(it) }
        }

        // Perform ItemSelectedListener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.schedule -> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, ScheduleCalendar::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }


        //Navigation Bar -Fragment
//        makeCurrentFragment(homeFragment)
//        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_menu)
//        NavigationBarView.OnItemSelectedListener{ it ->
//            when(it.itemId){
//                R.id.home -> makeCurrentFragment(homeFragment)
//                R.id.schedule -> makeCurrentFragment(scheduleFragment)
//            }
//            true
//        }


//        val bundle = Bundle()
//        userViewModel.allUsers.observe(this, Observer<List<UserModel>>() { users ->
//            bundle.putString("name", "${users[0].userName}")
//            bundle.putString("height", "${users[0].height}")
//            bundle.putString("weight", "${users[0].weight}")
//            bundle.putString("bmi", "${users[0].bmi}")
//
//            val myObj = HomeFragment()
//            myObj.setArguments(bundle)
//
//            Log.d("obj", myObj.arguments.toString())
//        })

        displayUserDetails()
        exerciseDetails()
        //Barchart
        myBarChart = findViewById(R.id.BarChart)
        populateBarChart()

    }

//    private fun makeCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fl_wrapper, fragment)
//            commit()
//        }

    fun selectButtonOnClick(view: View){
        val intent = Intent(this, Exercise::class.java)
        intent.putExtra("exerciseId",1)
        startActivity(intent)
    }

    fun scheduleButtonOnClick(view:View){
        val myIntent = Intent(this, ScheduleCalendar::class.java)
        startActivity(myIntent)
    }

    fun displayUserDetails() {
        val tvName = findViewById<TextView>(R.id.name)
        val textHeight = findViewById<TextView>(R.id.height)
        val textWeight = findViewById<TextView>(R.id.weight)
        val tvBmi = findViewById<TextView>(R.id.bmi)

        userViewModel.allUsers.observe(this, Observer<List<UserModel>>() { users ->
            for (user in users) {
                if(user.id.equals(userID)) {
                    tvName.append(user.userName)
                    textHeight.append(user.height.toString())
                    textWeight.append(user.weight.toString())
                    tvBmi.append(user.bmi.toString())
                }
            }
        })
    }


    fun exerciseDetails(){
        var totalCalories = 0f
        val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)
        tvTotalCalories.text = ""
        completeExerciseModel.completedExercise.observe(this, Observer<List<CompletedExerciseModel>>(){ completedExercise->
            for ( comEx in completedExercise){
                if (comEx.userId.equals(userID)){
                    totalCalories += comEx.total_calories
                }
            }
            tvTotalCalories.append(totalCalories.toString())
        })
    }


    //Barchart
    fun populateBarChart() {
        val caloriesList: ArrayList<BarEntry> = ArrayList()
        val yValue: MutableMap<Int, Float> = TreeMap()
        val xValue: MutableMap<Int, String> = TreeMap()
        exNameList = ArrayList()
        completeExerciseModel.completedExercise.observe(this, Observer<List<CompletedExerciseModel>>() { completedExercise ->
            yValue.put(1, 100F)
            for (eachEx in completedExercise) {
                val comExExerciseId = eachEx.exerciseId
                val calories = eachEx.total_calories
                if (eachEx.userId.equals(userID)) {
                    if (yValue.containsKey(comExExerciseId)) {
                        val totalCal = calories + yValue.getValue(comExExerciseId)
                        yValue.put(comExExerciseId, totalCal)
                    } else {
                        yValue.put(comExExerciseId, calories);
                    }
                }
            }
            exerciseViewModel.allExercise.observe(this, Observer<List<ExerciseModel>>() { exercise ->
                for ( ex in exercise){
                    val exId = ex.id
                    val exName = ex.name
                    xValue.put(exId, exName)
                }
                for ((keyY,valueY) in yValue){
                    for((keyX,valueX) in xValue) {
                        if (keyY.equals(keyX)){
                            exNameList.add(valueX)
                        }
                    }
                }

                for ((keyEntries, valueEntries) in yValue){
                    caloriesList.add(BarEntry(valueEntries, keyEntries-1))
                }

                val barDataSet = BarDataSet(caloriesList, "${exNameList.toString()}")
                //set a template coloring
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
                val data = BarData(exNameList, barDataSet)
                myBarChart.data = data
                //setting the x-axis
                val xAxis: XAxis = myBarChart.xAxis
                //calling methods to hide x-axis gridlines
                myBarChart.axisLeft.setDrawGridLines(false)
                xAxis.setDrawGridLines(false)
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
                myBarChart.getAxisRight().setEnabled(false);
                //xAxis.setDrawAxisLine(false)
                myBarChart.setBackgroundColor(resources.getColor(R.color.white))
                //remove legend
                myBarChart.legend.isEnabled = true

                //add animation
                myBarChart.animateY(3000)
                //refresh the chart
                myBarChart.invalidate()
            })
        })
    }
}

//reference:
//        //database testing: data insertion
//        val user = UserModel(4,"jiayi","123",155.3f,45f,18.9f)
//        userViewModel.createNewUser(user = user)
//
//        val userstest = ""
//
//        userViewModel.allUsers.observe(this, Observer{ users ->
////            Log.d("db insertion testing", users[3].userName)
////            userstest = user[1]
//
////            binding.BarChart = valuefromdb
//        })