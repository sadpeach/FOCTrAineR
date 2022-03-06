package com.example.foctrainer.entity

data class CompletedExerciseCalendarModel(val schedule_id:Int,val user_id:Int,val exercise_id:Int,val completed_dateTime:String,val isCompleted:Int) {
}