package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Query
import com.example.foctrainer.entity.ScheduleExerciseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleExerciseMapper {

    @Query("SELECT name,ScheduleTable.id AS scheduleId,user_id,exercise_id,startDateTime,notes,no_of_sets,title FROM ScheduleTable INNER JOIN ExerciseTable ON ScheduleTable.exercise_id=ExerciseTable.id WHERE date(startDateTime) =:selectedDate AND user_id=:userId ")
    fun getScheduleExercise(selectedDate:String,userId:Int):Flow<List<ScheduleExerciseModel>>
}