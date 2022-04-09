package com.example.foctrainer.mapper

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseMapper{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNewExercise(exercise: ExerciseModel)

    @Query("SELECT * FROM ExerciseTable")
    fun getAllExercises(): Flow<List<ExerciseModel>>

    @Query("SELECT name FROM ExerciseTable WHERE id=:exerciseId ")
    fun getExerciseNameById(exerciseId:Int): Flow<String>
}
