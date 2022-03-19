package com.example.foctrainer.exercise

import android.util.Log

class RepetitionCounter {

    private val TAG = "RepetitionCounter"
    private var className: String? = null
    private var enterThreshold = 0f
    private var exitThreshold = 0f

    private var numRepeats = 0
    private var poseEntered = false

    // These thresholds can be tuned in conjunction with the Top K values in {@link PoseClassifier}.
    // The default Top K value is 10 so the range here is [0-10].
    constructor(className: String?): this(className,6f,4f){
    }

    constructor(className: String?, enterThreshold: Float, exitThreshold: Float) {
        Log.d(TAG,"initialising repcounter")
        this.className = className
        this.enterThreshold = enterThreshold
        this.exitThreshold = exitThreshold
        numRepeats = 0
        poseEntered = false
    }

    /**
     * Adds a new Pose classification result and updates reps for given class.
     *
     * @param classificationResult {link ClassificationResult} of class to confidence values.
     * @return number of reps.
     */
    fun addClassificationResult(classificationResult: ClassificationResult): Int {
        val poseConfidence = classificationResult.getClassConfidence(className)

        if (!poseEntered) {
            poseEntered = poseConfidence > enterThreshold
            return numRepeats
        }
        if (poseConfidence < exitThreshold) {
            numRepeats++
            poseEntered = false
        }
        Log.d(TAG, className + " count: " + numRepeats)

        return numRepeats
    }

    fun getClassName(): String? {
        return className
    }

    fun getNumRepeats(): Int {
        return numRepeats
    }
}