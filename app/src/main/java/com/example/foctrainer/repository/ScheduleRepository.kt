package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.mapper.ScheduleMapper
import kotlinx.coroutines.flow.Flow

class ScheduleRepository (private val scheduleMapper: ScheduleMapper) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createNewDate(date: ScheduleModel){
        scheduleMapper.createNewDate(date)
    }

    fun getScheduleByDate (selectedDate:String): Flow<List<ScheduleModel>>  {
        return scheduleMapper.getScheduleByDate(selectedDate)
    }

    val allDates: Flow<List<ScheduleModel>> = scheduleMapper.getAllDates()
}