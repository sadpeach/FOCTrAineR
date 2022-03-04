package com.example.foctrainer.databaseConfig

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.foctrainer.entity.ExerciseModel
import java.lang.Exception

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Foctrainer.db"

        //tables
        private const val EXERCISE_TABLE = "ExerciseTable"
        private const val ML_CONFIG_TABLE = "MLConfigTable"

        //ExcerciseTable
        private const val EXERCISE_ID = "_id"
        private const val EXERCISE_NAME = "name"
        private const val EXERCISE_ML_ID = "mlId"

        //MLConfigTable
        private const val ML_CONFIG_ID = "_id"
        private const val ML_CONFIG_COORDINATES = "coordinates"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_ML_CONFIG_TABLE = ("CREATE TABLE " + ML_CONFIG_TABLE + " (" + ML_CONFIG_ID
                + " INTEGER PRIMARY KEY," + ML_CONFIG_COORDINATES + " TEXT" +")")

        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + EXERCISE_TABLE + " ( " + EXERCISE_ID
                + " INTEGER PRIMARY KEY," + EXERCISE_NAME + " TEXT, " + EXERCISE_ML_ID + " INTEGER, "
                + "FOREIGN KEY (" + EXERCISE_ML_ID + ")" + " REFERENCES " + ML_CONFIG_TABLE +
                " (" + ML_CONFIG_ID + ") ON DELETE CASCADE ON UPDATE NO ACTION)")

        try {
            db?.execSQL("PRAGMA foreign_keys=ON");
            db?.execSQL(CREATE_ML_CONFIG_TABLE)
            db?.execSQL(CREATE_EXERCISE_TABLE)
        }catch (e:Exception){
            Log.d("database","database is not created")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $EXERCISE_TABLE")
        db!!.execSQL("DROP TABLE IF EXISTS $ML_CONFIG_TABLE")
        onCreate(db)
    }
}