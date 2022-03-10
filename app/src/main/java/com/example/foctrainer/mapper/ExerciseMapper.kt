package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Query
import com.example.foctrainer.entity.ExerciseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseMapper{

    @Query("SELECT * FROM ExerciseTable")
    fun getAllExercises(): Flow<List<ExerciseModel>>
}
