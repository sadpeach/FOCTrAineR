package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.databinding.ActivityMainBinding
import android.graphics.Color
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.viewModel.ExerciseViewModel
import com.example.foctrainer.viewModel.ExerciseViewModelFactory
import com.example.foctrainer.viewModel.UserViewModel
import com.example.foctrainer.viewModel.UserViewModelFactory
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

//    private val exerciseViewModel: ExerciseViewModel by viewModels {
//        ExerciseViewModelFactory((application as FocTrainerApplication).exerciserepository)
//    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as FocTrainerApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //database testing: data insertion
        val user = UserModel(4,"jiayi","123",155.3f,45f,18.9f)
        userViewModel.createNewUser(user = user)

        userViewModel.allUsers.observe(this, Observer{ users ->
            Log.d("db insertion testing", users[3].userName)
        })


        val listView = findViewById<ListView>(R.id.workoutCategoriesListView)
        val names = arrayOf("Body Weight", "Strength Training", "Have some fun!")
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, names
        )
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "Selected this" + names[i], Toast.LENGTH_LONG).show()
        }

        val userContext = this
//        var userDB = UserDB(userContext)
//        var userInfoData = userDB.readUserData()
//        val tvName = findViewById<TextView>(R.id.name)
//        tvName.text = ""
////        for (i in 0..(userInfoData.size-1)){
////            tvName.append(userInfoData.get(i).name.toString())
////        }
//
//        setLineChartData()

    }

    fun selectButtonOnClick(view: View){
        val intent = Intent(this, Exercise::class.java)
        startActivity(intent)
    }


    //line Chart
    fun setLineChartData(){
        val lineChart = findViewById<LineChart>(R.id.lineChart)

        lineValuesList = ArrayList();
        lineValuesList.add(Entry(10f, 100F))
        lineValuesList.add(Entry(20f, 200F))
        lineValuesList.add(Entry(30f, 300F))
        lineValuesList.add(Entry(40f, 20F))
        lineValuesList.add(Entry(50f, 30F))

        lineDataSet = LineDataSet(lineValuesList, "WorkOut")
        dataLine = LineData(lineDataSet)
        lineChart.data = dataLine

        lineDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 15f
//        lineDataSet.setDrawFilled(true)

//        lineChart.setBackgroundColor(resources.getColor(R.color.white))
        lineChart.animateXY(2000,2000)

    }
}