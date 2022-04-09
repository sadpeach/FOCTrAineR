package com.example.foctrainer.repository

import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import kotlinx.coroutines.flow.Flow

class ExerciseRepository (private val exerciseMapper: ExerciseMapper) {

    suspend fun createNewExercise(exercise: ExerciseModel){
        exerciseMapper.createNewExercise(exercise)
    }

    val allExercise:  Flow<List<ExerciseModel>> = exerciseMapper.getAllExercises()

        fun getExerciseNameById (exerciseId:Int): Flow<String> {
         return exerciseMapper.getExerciseNameById(exerciseId)
    }

}