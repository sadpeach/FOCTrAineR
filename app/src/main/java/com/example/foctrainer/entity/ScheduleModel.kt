package com.example.foctrainer.entity

data class ScheduleModel (val id:Int,val userId: Int, val exerciseId:Int,
                          val startDateTime:String,val notes:String,val no_of_sets:Int,val title:String) {

}