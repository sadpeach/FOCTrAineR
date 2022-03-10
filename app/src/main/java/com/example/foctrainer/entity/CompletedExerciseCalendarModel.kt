package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "CompletedExerciseCalendarTable",
    primaryKeys= ["schedule_id","user_id","exercise_id","completed_dateTime"],
    foreignKeys =[
        ForeignKey(entity = CompletedExerciseModel::class,
            parentColumns = arrayOf("user_id","exercise_id","completed_dateTime"),
            childColumns = arrayOf("user_id","exercise_id","completed_dateTime")
        ),
        ForeignKey(entity = ScheduleModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("schedule_id")
        )
    ]
)
data class CompletedExerciseCalendarModel(
    @ColumnInfo(name="schedule_id") val schedule_id:Int,
    @ColumnInfo(name="user_id") val user_id:Int,
    @ColumnInfo(name="exercise_id") val exercise_id:Int,
    @ColumnInfo(name="completed_dateTime") val completed_dateTime:String,
    @ColumnInfo(name="isCompleted") val isCompleted:Int) {
}