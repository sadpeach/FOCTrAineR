package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "UserTable")
data class UserModel (
    @PrimaryKey (autoGenerate = true) val id:Int,
    @ColumnInfo(name="userName") val userName:String,
    @ColumnInfo(name="password") val password:String,
    @ColumnInfo(name="height") val height:Float,
    @ColumnInfo(name="weight") val weight:Float,
    @ColumnInfo(name="bmi") val bmi:Float) {
}