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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sqLiteHelper: SQLiteHelper

    companion object{

        fun getContext():Context{
            return Application().applicationContext
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("database","creating database")
        sqLiteHelper = SQLiteHelper(this)
//        addExercise()
    }

//    private fun addExercise(){
//        val exerciseModel = ExerciseModel(1,"testing1",1)
//        val result = sqLiteHelper.insertExercise(exerciseModel)
//        Log.d("database","insertion result"+result)
//    }

    fun selectButtonOnClick(view: View){
        val intent = Intent(this, Exercise::class.java)
        startActivity(intent)
    }
}