package com.example.foctrainer.utils

/**TODO: I am not sure what fields will be passed in here, but heres the base file for the calculator \
 * can modify accordingly!
 **/
class CaloriesCalculator(weight:Float,exerciseName:String,noOfExerciseCompleted:Int) {

    companion object{
        val TAG = "CaloriesCalculator"
        var calories = 0f
    }

    //TODO: Put in the formula here
    fun calculator(){

        //set calories in here for getCalories, no need to return value
    }

    fun getCalories(): Float{
        return calories
    }

}