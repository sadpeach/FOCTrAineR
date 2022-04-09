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

    fun getScheduleByDate (selectedDate:String,userId:Int): Flow<List<ScheduleModel>>  {
        return scheduleMapper.getScheduleByDate(selectedDate,userId)
    }

    val allDates: Flow<List<ScheduleModel>> = scheduleMapper.getAllDates()

    fun getDates (userId: Int):Flow<List<String>>{
        return scheduleMapper.getDates(userId)
    }

    fun getScheduledCountById (scheduleId:Int) : Flow<Int>  {
        return scheduleMapper.getScheduledCountById(scheduleId)
    }

    fun getScheduledExerciseById(scheduleId:Int,userId: Int): Flow<ScheduleModel>{
        return scheduleMapper.getScheduledExerciseById(scheduleId,userId)
    }
}