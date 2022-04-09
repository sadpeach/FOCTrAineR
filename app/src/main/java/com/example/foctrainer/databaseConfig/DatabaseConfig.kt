package com.example.foctrainer.databaseConfig

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.foctrainer.entity.*
import com.example.foctrainer.mapper.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [ExerciseModel::class,UserModel::class,ScheduleModel::class,CompletedExerciseModel::class], version = 1)
abstract class FocTrainerDatabase : RoomDatabase() {

    abstract fun exerciseMapper(): ExerciseMapper
    abstract fun userMapper():UserMapper
    abstract fun scheduleMapper():ScheduleMapper
    abstract fun completedExerciseMapper():CompletedExerciseMapper

    companion object {

        @Volatile
        private var INSTANCE: FocTrainerDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): FocTrainerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(context.applicationContext, FocTrainerDatabase::class.java, "FocTrainerDatabase.db")
                    .createFromAsset("FocTrainerDatabase.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    }