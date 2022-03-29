package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foctrainer.entity.ScheduleModel
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ScheduleMapper {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNewDate(date: ScheduleModel)

    @Query("SELECT * FROM ScheduleTable")
    fun getAllDates(): Flow<List<ScheduleModel>>

    @Query("SELECT * FROM ScheduleTable WHERE date(startDateTime) =:selectedDate AND user_id=:userId")
    fun getScheduleByDate(selectedDate:String,userId:Int): Flow<List<ScheduleModel>>

    @Query("SELECT startDateTime FROM ScheduleTable WHERE user_id=:userId")
    fun getDates(userId:Int): Flow<List<String>>

    @Query("SELECT no_of_sets FROM ScheduleTable WHERE id = :scheduleId")
    fun getScheduledCountById(scheduleId:Int): Flow<Int>

    @Query("SELECT * FROM ScheduleTable WHERE id = :scheduleId AND user_id=:userId ")
    fun getScheduledExerciseById(scheduleId:Int,userId: Int): Flow<ScheduleModel>
}