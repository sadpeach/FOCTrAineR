package com.example.foctrainer.mapper

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.foctrainer.MainActivity
import com.example.foctrainer.databaseConfig.SQLiteHelper
import com.example.foctrainer.entity.ExerciseModel
import java.lang.Exception

class ExerciseMapper(con:Context){

    companion object{
        private const val TABLE_NAME="ExerciseTable"

    }
    var sqlHelper: SQLiteHelper = SQLiteHelper(con)
    val db = sqlHelper.readableDatabase

    @SuppressLint("Range")
    fun getAllExercises():List<ExerciseModel>{
        val allExerciseModels : ArrayList<ExerciseModel> = ArrayList()
        val sqlQuery = "SELECT * FROM $TABLE_NAME"
        Log.d("DBRetrieve",sqlQuery)

        val cursor:Cursor?

        try {
            cursor = db.rawQuery(sqlQuery,null)
        }catch (e:Exception){
            e.printStackTrace()
            return ArrayList()
        }

        var id:Int
        var name:String
        var mlId:Int

        if (cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("_id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                mlId = cursor.getInt(cursor.getColumnIndex("mlId"))
                val exercise = ExerciseModel(id,name,mlId)
                allExerciseModels.add(exercise)
            }while(cursor.moveToNext())
        }

        return allExerciseModels
    }

//    fun getExercisesById(id:Int):Exercise{
//        val sqlQuery = "SELECT * FROM ExerciseTable WHERE _id equals" + id
//        return result
//    }


}