package com.example.foctrainer.utils


import androidx.appcompat.app.AppCompatActivity


/**TODO: I am not sure what fields will be passed in here, but heres the base file for the calculator \
 * can modify accordingly!
 **/
class CaloriesCalculator(weight:Float,exerciseName:String,noOfExerciseCompleted:Int) : AppCompatActivity() {

    companion object{
        val TAG = "CaloriesCalculator"
        var calories = 0f
    }

    var userWeightKg = weight
    var userWeightLbs = 0.00

    var no_completed_sets = noOfExerciseCompleted

    var exName = exerciseName

    var standard_Ex = 0.00

    //TODO: Put in the formula here
    fun calculator(){

        //set calories in here for getCalories, no need to return value

        //1. weight in lbs

        userWeightLbs = userWeightKg * 2.2

        //2. no of sets - get from completedExRepo


        //3. standard version

        if (exName == "pushups") {
            standard_Ex = 0.0027
            calories = (userWeightLbs * no_completed_sets * standard_Ex).toFloat()
        }

        if (exName == "squats") {
            // 0.32 is the ave calories burnt for 1 squat for moderate effort
            calories =  (no_completed_sets * 0.32).toFloat()
        }

        //4. total calories for completedExID



        //5. update to the DB for userID + exID + completedDT


    }

    fun getCalories(): Float{
        return calories
    }

}