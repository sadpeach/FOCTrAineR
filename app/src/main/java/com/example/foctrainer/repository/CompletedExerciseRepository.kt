package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.mapper.CompletedExerciseMapper
import kotlinx.coroutines.flow.Flow

class CompletedExerciseRepository (private val completedExerciseMapper: CompletedExerciseMapper) {
    val completedExercise: Flow<List<CompletedExerciseModel>> =
        completedExerciseMapper.getAllCompletedExercise()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNewCompletedExercise(completedExercise: CompletedExerciseModel) {
        completedExerciseMapper.insertNewCompletedExercise(completedExercise)
    }
//    val chartSummary: Flow<List<String>> = completedExerciseMapper.getChartSummary()
}