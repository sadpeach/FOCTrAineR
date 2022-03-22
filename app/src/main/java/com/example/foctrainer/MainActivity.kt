package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.databinding.ActivityMainBinding
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.fragments.HomeFragment
import com.example.foctrainer.fragments.ScheduleFragment
import com.example.foctrainer.viewModel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.ColorTemplate

import android.widget.TextView
import com.github.mikephil.charting.data.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lineValuesList: ArrayList<Entry>
    private val exList = ArrayList<ExerciseModel>()
    private lateinit var exerciseAdapter: RecyclerAdapter

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    //BarChart
    private lateinit var myBarChart: BarChart

    val homeFragment = HomeFragment()
    val scheduleFragment = ScheduleFragment()

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

        //Navigation Bar -Fragment
//        makeCurrentFragment(homeFragment)
//        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
//        bottomNavView.setOnNavigationItemSelectedListener{ it ->
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


//        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
//        exerciseAdapter = RecyclerAdapter(exList)
//        val layoutManager = LinearLayoutManager(applicationContext)
//        recyclerView.layoutManager = layoutManager
//        recyclerView.itemAnimator = DefaultItemAnimator()
//        recyclerView.adapter = exerciseAdapter

        displayUserDetails()
//        exerciseDetails()
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

    //Remove For Fragment
//    private fun makeCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.wrapper, fragment)
//            commit()
//        }
    fun selectButtonOnClick(view: View){
        val intent = Intent(this, Exercise::class.java)
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
        //adding values
        val myBarEntries: ArrayList<BarEntry> = ArrayList()
        var i = 0
        val xAxis: ArrayList<String> = ArrayList()
        completeExerciseModel.completedExercise.observe(this, Observer<List<CompletedExerciseModel>>() { completedExercise ->
            for (eachEx in completedExercise) {
                Log.d("PLS", eachEx.toString())
                val comExUserId = eachEx.userId
                val comExExerciseId = eachEx.exerciseId
                val calories = eachEx.total_calories
                if (comExUserId == userID) {
                    myBarEntries.add(BarEntry(comExExerciseId.toFloat(), calories))
                }
            }

            val barDataSet = BarDataSet(myBarEntries, "Work Out Summary")
            //set a template coloring
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            val data = BarData(barDataSet)
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
            myBarChart.description.isEnabled = false

            //add animation
            myBarChart.animateY(3000)
            //refresh the chart
            myBarChart.invalidate()
        })
    }
}