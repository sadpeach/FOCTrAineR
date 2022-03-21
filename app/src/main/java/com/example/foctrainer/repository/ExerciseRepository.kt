package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import kotlinx.coroutines.flow.Flow

class ExerciseRepository (private val exerciseMapper: ExerciseMapper) {

    val allExercise:  Flow<List<ExerciseModel>> = exerciseMapper.getAllExercises()

    suspend fun createNewExercise(exercise: ExerciseModel){
        exerciseMapper.createNewExercise(exercise)
    }

    fun getExerciseNameById(exerciseId:Int): Flow<String> {
        return exerciseMapper.getExerciseNameById(exerciseId)
    }


//     fun getExerciseNameById (exerciseId:Int):String{
//        return exerciseMapper.getExerciseNameById(exerciseId)
//    }

}