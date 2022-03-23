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

    @Query("SELECT * FROM ScheduleTable WHERE date(startDateTime) =:selectedDate")
    fun getScheduleByDate(selectedDate:String): Flow<List<ScheduleModel>>

    @Query("SELECT startDateTime FROM ScheduleTable")
    fun getDates(): Flow<List<String>>
}