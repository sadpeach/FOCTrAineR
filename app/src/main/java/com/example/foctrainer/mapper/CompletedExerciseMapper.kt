package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Query
import com.example.foctrainer.entity.CompletedExerciseModel
import kotlinx.coroutines.flow.Flow


@Dao
interface CompletedExerciseMapper {
    @Query("SELECT * FROM CompletedExerciseTable")
    fun getAllCompletedExercise(): Flow<List<CompletedExerciseModel>>
}