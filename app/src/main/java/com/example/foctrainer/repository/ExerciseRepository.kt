package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper
import kotlinx.coroutines.flow.Flow

class ExerciseRepository (private val exerciseMapper: ExerciseMapper) {



    val allExercise:  Flow<List<ExerciseModel>> = exerciseMapper.getAllExercises()

//    fun getExerciseNameById(exerciseId:Int): Flow<String> {
//        return exerciseMapper.getExerciseNameById(exerciseId)
//    }

//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
        fun getExerciseNameById (exerciseId:Int):String {
         return exerciseMapper.getExerciseNameById(exerciseId)
    }

}