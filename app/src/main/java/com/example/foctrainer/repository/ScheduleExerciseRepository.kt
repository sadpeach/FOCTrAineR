package com.example.foctrainer.repository

import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.ScheduleExerciseModel
import com.example.foctrainer.mapper.ScheduleExerciseMapper
import kotlinx.coroutines.flow.Flow

class ScheduleExerciseRepository(private val scheduleExerciseMapper: ScheduleExerciseMapper) {

    fun getScheduleExercise(selectedDate:String,userId:Int): Flow<List<ScheduleExerciseModel>> {
        return scheduleExerciseMapper.getScheduleExercise(selectedDate,userId)
    }

    }