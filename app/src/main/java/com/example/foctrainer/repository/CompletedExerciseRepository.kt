package com.example.foctrainer.repository

import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.mapper.CompletedExerciseMapper
import kotlinx.coroutines.flow.Flow

class CompletedExerciseRepository (private val completedExerciseMapper: CompletedExerciseMapper) {
    val completedExercise: Flow<List<CompletedExerciseModel>> = completedExerciseMapper.getAllCompletedExercise()
}