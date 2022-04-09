package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey

@Entity(tableName = "ExerciseTable")
data class ExerciseModel (
    @PrimaryKey (autoGenerate = true)val id: Int,
    @ColumnInfo(name = "name")val name: String){
}

