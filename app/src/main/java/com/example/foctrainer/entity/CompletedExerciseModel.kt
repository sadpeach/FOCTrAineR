package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "CompletedExerciseTable",
    primaryKeys= ["user_id","exercise_id","completed_dateTime"],
    foreignKeys =[
        ForeignKey(entity = UserModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user_id")
        ),
        ForeignKey(entity = ExerciseModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise_id")
        )
    ]
)
data class CompletedExerciseModel (
    @ColumnInfo(name="user_id") val userId:Int,
    @ColumnInfo(name="exercise_id") val exerciseId:Int,
    @ColumnInfo(name="completed_dateTime") val completed_dateTime:String,
    @ColumnInfo(name="total_calories") val total_calories:Float,
    @ColumnInfo(name="no_completed_sets") val no_completed_sets:Int? ) {
}