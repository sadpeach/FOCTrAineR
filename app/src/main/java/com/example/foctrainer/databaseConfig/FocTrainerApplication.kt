package com.example.foctrainer.databaseConfig

import android.app.Application
import com.example.foctrainer.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FocTrainerApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { FocTrainerDatabase.getDatabase(this, applicationScope) }
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseMapper()) }
    val userRepository by lazy { UserRepository(database.userMapper()) }
    val completedExerciseRepository by lazy { CompletedExerciseRepository(database.completedExerciseMapper()) }
    val scheduleRepository by lazy { ScheduleRepository(database.scheduleMapper()) }
    val scheduleExerciseRepository by lazy { ScheduleExerciseRepository(database.scheduleExerciseMapper()) }
}