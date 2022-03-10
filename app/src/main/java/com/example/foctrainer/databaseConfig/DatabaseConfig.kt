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

@Database(entities = [ExerciseModel::class,MLConfigModel::class,UserModel::class,ScheduleModel::class,CompletedExerciseModel::class,CompletedExerciseCalendarModel::class], version = 1)
abstract class FocTrainerDatabase : RoomDatabase() {

    abstract fun exerciseMapper(): ExerciseMapper
    abstract fun mlConfigMapper(): MLConfigMapper
    abstract fun userMapper():UserMapper
    abstract fun scheduleMapper():ScheduleMapper
    abstract fun completedExerciseCalenderMapper():CompletedExerciseCalendarMapper
    abstract fun completedExerciseMapper():CompletedExerciseMapper

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var exerciseMapper = database.exerciseMapper()

//                    // Delete all content here.
//                    exerciseMapper.deleteAll()
//
//                    // Add sample words.
//                    var word = Word("Hello")
//                    wordDao.insert(word)
//                    word = Word("World!")
//                    wordDao.insert(word)
//
//                    // TODO: Add your own words!
//                    word = Word("TODO!")
//                    wordDao.insert(word)
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FocTrainerDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): FocTrainerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    FocTrainerDatabase::class.java,
//                    "FocTrainerDatabase"
//                )
//                    .addCallback(WordDatabaseCallback(scope))
//                    .build()
                val instance = Room.databaseBuilder(context.applicationContext, FocTrainerDatabase::class.java, "FocTrainerDatabase.db")
                    .createFromAsset("FocTrainerDatabase.db")
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    }