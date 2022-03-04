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
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sqLiteHelper: SQLiteHelper

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
}