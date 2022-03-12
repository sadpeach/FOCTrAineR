package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.databinding.ActivityMainBinding
import android.graphics.Color
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.viewModel.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lineValuesList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var dataLine: LineData
    var userID = 0
//    var exerciseID = 0
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

        userViewModel.allUsers.observe(this, Observer{ users ->
            Log.d("db insertion testing", users[3].userName)
        })

        //database testing: data insertion
        val user = UserModel(4,"jiayi","123",155.3f,45f,18.9f)
        userViewModel.createNewUser(user = user)



        displayUserDetails()
        exerciseDetails()
        setLineChartData()
    }

    fun selectButtonOnClick(view: View){
        val intent = Intent(this, Exercise::class.java)
        startActivity(intent)
    }

    fun displayUserDetails(){
        val tvName = findViewById<TextView>(R.id.name)
        val textEditHeight = findViewById<EditText>(R.id.editHeight)
        val textEditWeight = findViewById<EditText>(R.id.editWeight)
        val tvBmi = findViewById<TextView>(R.id.bmi)
        tvName.text = ""
        tvBmi.text = ""

        userViewModel.allUsers.observe(this, Observer<List<UserModel>>() { users ->
            userID = users[0].id
            tvName.append(users[0].userName)
            textEditHeight.setText(users[0].height.toString())
            textEditWeight.setText(users[0].weight.toString())
            tvBmi.append(users[0].bmi.toString())
        })
    }
    fun exerciseDetails(){
        //Exercise
        val exlist = ArrayList<String>()
        val exerciseListView = findViewById<ListView>(R.id.workoutCategoriesListView)

        //get recycle view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewModel.allExercise.observe(this, Observer<List<ExerciseModel>>(){ exercise->
            for (ex in exercise){
                exerciseName = ex.name
//                exerciseID = ex.id
                exlist.add("$exerciseName")
            }
            //attach the array adapter to the list view
            exerciseListView.adapter = ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,exlist)

//            recyclerView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exlist)
        })
        exerciseListView.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "You have selected " + exlist[i], Toast.LENGTH_LONG).show()
        }

        var totalCalories = 0f
        val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)
        tvTotalCalories.text = ""
        var test1 = "hey"
        completeExerciseModel.completedExercise
        completeExerciseModel.completedExercise.observe(this, Observer<List<CompletedExerciseModel>>(){ completedExercise->
            for ( comEx in completedExercise){
                val exUserId = comEx.userId
                if (exUserId == userID){
                    totalCalories += comEx.total_calories
                }
            }
            test1 = totalCalories.toString();
            tvTotalCalories.append(totalCalories.toString())
            Log.d("0009",test1)
        })
    }

    //line Chart
    fun setLineChartData(){
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        var lineValuesList : ArrayList<Entry> = ArrayList();

        completeExerciseModel.completedExercise.observe(this, { completedExercise->
            for (eachEx in completedExercise){
                val comExUserId = eachEx.userId
                val comExExerciseId = eachEx.exerciseId
                val calories = eachEx.total_calories
                if (comExUserId == userID){
                    Log.d("IDW1", comExExerciseId.toString())
                    Log.d("IDW2", calories.toString())
//                    test.add(comExExerciseId.toString())
                    lineValuesList.add(Entry(comExExerciseId.toFloat(), calories))
                }

            }


        Log.d("testing", lineValuesList.toString())
//        lineValuesList.add(Entry(10f, 100F))
//        lineValuesList.add(Entry(20f, 200F))
//        lineValuesList.add(Entry(30f, 300F))
//        lineValuesList.add(Entry(40f, 20F))
//        lineValuesList.add(Entry(50f, 30F))

        lineDataSet = LineDataSet(lineValuesList, "WorkOut")
        dataLine = LineData(lineDataSet)
        lineChart.data = dataLine

        lineDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 15f
//        lineDataSet.setDrawFilled(true)

//        lineChart.setBackgroundColor(resources.getColor(R.color.white))
        lineChart.animateXY(2000,2000)
        })
    }
}