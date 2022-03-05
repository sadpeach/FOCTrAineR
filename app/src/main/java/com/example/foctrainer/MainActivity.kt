package com.example.foctrainer

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.databaseConfig.SQLiteHelper
import com.example.foctrainer.databinding.ActivityMainBinding
import java.io.IOException
import java.io.InputStream
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.foctrainer.databaseConfig.UserDB
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var lineValuesList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var dataLine: LineData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sqLiteHelper = SQLiteHelper(this)
        val db = sqLiteHelper.readableDatabase

        val data:InputStream ?= checkFileExists()

        if (data != null) {
            readData(data,db)
        }

//        testing database retrieval
//        val exemap = ExerciseMapper(this)
//        val allexe: List <ExerciseModel> = exemap.getAllExercises()
//        for (exe in allexe){
//            Log.d("DB Retreive",exe.getName())
//        }

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
        var userDB = UserDB(userContext)
        var userInfoData = userDB.readUserData()
        val tvName = findViewById<TextView>(R.id.name)
        tvName.text = ""
        for (i in 0..(userInfoData.size-1)){
            tvName.append(userInfoData.get(i).name.toString())
        }

        setLineChartData()

    }


    private fun checkFileExists(): InputStream? {

        var data : InputStream

        try {
            data = this.resources.openRawResource(R.raw.data)
            return data
        }
        catch (e:IOException){
            Log.d("Read data","reading data failed")
            e.printStackTrace()
            return null
        }
    }

    private fun readData(data:InputStream, db: SQLiteDatabase){

        var br = BufferedReader(InputStreamReader(data))

        try {
            var line = br.readLine()
            while (line != null){
                Log.d("ACTIONSQL","Line $line")
                db.execSQL(line)
                line = br.readLine()
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            db.endTransaction()
        }
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