package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.mapper.ExerciseMapper
import com.example.foctrainer.mapper.ScheduleMapper
import java.util.concurrent.Flow

class ScheduleRepository (private val scheduleMapper: ScheduleMapper) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createNewDate(date: ScheduleModel){
        scheduleMapper.createNewDate(date)

    }
    val allDates: kotlinx.coroutines.flow.Flow<List<ScheduleModel>> = scheduleMapper.getAllDates()
}