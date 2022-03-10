package com.example.foctrainer.repository

import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import kotlinx.coroutines.flow.Flow

class ExerciseRepository (private val exerciseMapper: ExerciseMapper) {
    val allExercise:  Flow<List<ExerciseModel>> = exerciseMapper.getAllExercises()
}