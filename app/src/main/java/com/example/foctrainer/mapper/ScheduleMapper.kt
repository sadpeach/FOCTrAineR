package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.entity.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleMapper {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNewDate(date: ScheduleModel)

    @Query("SELECT * FROM ScheduleTable")
    fun getAllDates(): Flow<List<ScheduleModel>>
}