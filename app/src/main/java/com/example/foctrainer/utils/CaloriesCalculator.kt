package com.example.foctrainer.utils

class CaloriesCalculator{

    private var calories = 0f

    constructor(weight:Float,exerciseName:String,noOfExerciseCompleted:Int){
        val userWeightLbs = weight * 2.2

        if (exerciseName == "pushups") {
            this.calories = (userWeightLbs * noOfExerciseCompleted * 0.0027).toFloat()
        }

        if (exerciseName == "squats") {
            this.calories =  (noOfExerciseCompleted * 0.32).toFloat()
        }
    }

    fun getCalories(): Float{
        return this.calories
    }

}