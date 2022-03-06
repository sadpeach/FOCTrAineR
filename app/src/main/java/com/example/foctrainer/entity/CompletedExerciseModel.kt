package com.example.foctrainer.entity

data class CompletedExerciseModel (val userId:Int,val exerciseId:Int,val completed_dateTime:String, val total_calories:Float,val no_completed_sets:Int ) {
}