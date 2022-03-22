package com.example.foctrainer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.adapters.AdapterViewBindingAdapter.setOnItemSelectedListener
import androidx.databinding.adapters.AutoCompleteTextViewBindingAdapter.setOnItemSelectedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityMainBinding
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.fragments.HomeFragment
import com.example.foctrainer.fragments.ScheduleFragment
import com.example.foctrainer.viewModel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lineValuesList: ArrayList<Entry>
    private val exList = ArrayList<ExerciseModel>()
    private lateinit var exerciseAdapter: RecyclerAdapter

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    //BarChart
    private lateinit var myBarChart: BarChart

//    val homeFragment = HomeFragment()
//    val scheduleFragment = ScheduleFragment()

    lateinit var  caloriesList: ArrayList<BarEntry>
    lateinit var exNameList : ArrayList<String>
    var userID = 0
    var exerciseName = ""

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as FocTrainerApplication).userRepository)
    }

    private val completeExerciseModel: CompletedExerciseViewModel by viewModels {
        CompletedExerciseViewModelFactory((application as FocTrainerApplication).completedExerciseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)

        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.home)

        // Perform ItemSelectedListener


        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    true

                }
                R.id.schedule -> {
                    // Respond to navigation item 2 click
                    Log.d("Schedule", "schedulewoo")
                    val intent = Intent(this, ScheduleTest::class.java)
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


        //database testing: data insertion
        val user = UserModel(4,"jiayi","123",155.3f,45f,18.9f)
        userViewModel.createNewUser(user = user)


        userViewModel.allUsers.observe(this, Observer{ users ->
            Log.d("db insertion testing", users[3].userName)
        })



//        completeExerciseModel.chartSummary.observe(this, Observer { ex->
//            Log.d("Chart", ex.toString())
//        })

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

        val catlist = ArrayList<String>()
         exerciseViewModel.allExercise.observe(this, Observer<List<ExerciseModel>>(){ exercise->
            for (ex in exercise){
                exerciseName = ex.name
                catlist.add("$exerciseName")
            }})

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecyclerAdapter(catlist)
        }
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
        val myIntent = Intent(this, PlannerActivity::class.java)
        startActivity(myIntent)
    }

    fun displayUserDetails() {
        val tvName = findViewById<TextView>(R.id.name)
        val textEditHeight = findViewById<EditText>(R.id.editHeight)
        val textEditWeight = findViewById<EditText>(R.id.editWeight)
        val tvBmi = findViewById<TextView>(R.id.bmi)

        tvName?.text = ""
        tvBmi?.text = ""

        userViewModel.allUsers.observe(this, Observer<List<UserModel>>() { users ->
            userID = users[0].id
            tvName.append(users[0].userName)
            textEditHeight.setText(users[0].height.toString())
            textEditWeight.setText(users[0].weight.toString())
            tvBmi.append(users[0].bmi.toString())
            Log.d("WHY", users[0].toString())
        })
    }


    fun exerciseDetails(){
        var totalCalories = 0f
        val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)
        tvTotalCalories.text = ""
        completeExerciseModel.completedExercise.observe(this, Observer<List<CompletedExerciseModel>>(){ completedExercise->
            for ( comEx in completedExercise){
                val exUserId = comEx.userId
                if (exUserId == userID){
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
                val comExUserId = eachEx.userId
                val comExExerciseId = eachEx.exerciseId
                val calories = eachEx.total_calories
                if (comExUserId == userID) {
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
//                Log.d("q4X", xValue.toString())
//                Log.d("q5Y", yValue.toString())
//                Log.d("q6CaloriesList", caloriesList.toString())
//                Log.d("q7ExerciseName", exNameList.toString())
                val barDataSet = BarDataSet(caloriesList, "${exNameList.toString()}")
                //set a template coloring
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
                //[Ex1 , Ex2 ]
                val data = BarData(exNameList, barDataSet)
                myBarChart.data = data
                //setting the x-axis
                val xAxis: XAxis = myBarChart.xAxis
                //calling methods to hide x-axis gridlines
                myBarChart.axisLeft.setDrawGridLines(false)
                xAxis.setDrawGridLines(false)
                //xAxis.setDrawAxisLine(false)
                myBarChart.setBackgroundColor(resources.getColor(R.color.white))
                //remove legend
                myBarChart.legend.isEnabled = true

                //remove description label
//            myBarChart.description.isEnabled = false

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