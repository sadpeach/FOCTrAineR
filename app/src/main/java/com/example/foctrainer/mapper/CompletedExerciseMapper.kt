package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.UserModel
import kotlinx.coroutines.flow.Flow


@Dao
interface CompletedExerciseMapper {
    @Query("SELECT * FROM CompletedExerciseTable")
    fun getAllCompletedExercise(): Flow<List<CompletedExerciseModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCompletedExercise(completedExercise: CompletedExerciseModel)

}