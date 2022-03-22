package com.example.foctrainer.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ScheduleTable",
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
data class ScheduleModel(
    @PrimaryKey (autoGenerate = true) val id:Int,
    @ColumnInfo(name="user_id") val userId: Int,
    @ColumnInfo(name="exercise_id") val exerciseId:Int,
    @ColumnInfo(name="startDateTime") val startDateTime:String,
    @ColumnInfo(name="notes") val notes:String?,
    @ColumnInfo(name="no_of_sets") val no_of_sets: Int,
    @ColumnInfo(name="title") val title:String) {

}