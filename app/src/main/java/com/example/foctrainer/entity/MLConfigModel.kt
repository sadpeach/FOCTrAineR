package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MLConfigTable")
data class MLConfigModel (
    @PrimaryKey (autoGenerate = true) val id:Int,
    @ColumnInfo(name="coordinates") val coordinates:String) {
}