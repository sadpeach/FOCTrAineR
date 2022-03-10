package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Query
import com.example.foctrainer.entity.MLConfigModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MLConfigMapper {
    @Query("SELECT * FROM MLConfigTable")
    fun getAllConfigs(): Flow<List<MLConfigModel>>
}