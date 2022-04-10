package com.example.foctrainer.entity

data class ScheduleExerciseModel(

    val name:String,
    val scheduleId:Int,
    val user_id:Int,
    val exercise_id:Int,
    val startDateTime:String,
    val notes:String,
    val no_of_sets:Int,
    val title:String

)