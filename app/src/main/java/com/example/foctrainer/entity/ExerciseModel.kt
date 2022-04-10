package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ExerciseTable")
data class ExerciseModel (
    @PrimaryKey (autoGenerate = true)val id: Int,
    @ColumnInfo(name = "name")val name: String){
}

