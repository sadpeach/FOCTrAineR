package com.example.foctrainer.exercise

import android.content.Context
import android.media.ToneGenerator

import android.media.AudioManager

import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.viewModel.ExerciseViewModel
import com.example.foctrainer.viewModel.ExerciseViewModelFactory
import com.google.mlkit.vision.common.PointF3D

import com.google.mlkit.vision.pose.Pose
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList
import com.example.foctrainer.R

import android.widget.TextView
import com.example.foctrainer.MainActivity
import android.app.Activity





class PoseClassifierProcessor {
    private val TAG = "PoseClassifierProcessor"
    private val POSE_SAMPLES_FILE = "pose_samples.csv"

    private val NUM_LANDMARKS = 33
    private val NUM_DIMS = 3

    private val PUSHUPS_CLASS = "pushups_down"
    private val SQUATS_CLASS = "squats_down"
    private val POSE_CLASSES = arrayOf(
        PUSHUPS_CLASS, SQUATS_CLASS
    )

    private var isStreamMode = false
    private var emaSmoothing: EMASmoothing? = null
    private var repCounters: MutableList<RepetitionCounter>? = null
    private var poseClassifier: PoseClassifier? = null
    private var lastRepResult: String? = null
    private var selectedExerciseId: Int = -1
    private var selectedExerciseName : String? = null
    private var count: Int = 0;

    @WorkerThread
    constructor(context: Context, isStreamMode: Boolean,selectedExerciseId:Int,selectedExerciseName:String) {
        com.google.common.base.Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        this.isStreamMode = isStreamMode
        if (isStreamMode) {
            emaSmoothing = EMASmoothing()
            repCounters = ArrayList()
            lastRepResult = ""
        }
        this.selectedExerciseId = selectedExerciseId
        this.selectedExerciseName = selectedExerciseName
        loadPoseSamples(context,selectedExerciseId,selectedExerciseName)
    }

    private fun loadPoseSamples(context: Context,selectedExerciseId: Int,selectedExerciseName: String) {
        Log.d(TAG, "Loading pose sample..$selectedExerciseName")
        val poseSamples: MutableList<PoseSample?> = ArrayList()

        //load from csv
        try {
            val reader = BufferedReader(InputStreamReader(context.assets.open(POSE_SAMPLES_FILE)))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
            var name = ""
            for (csvRecord in csvParser){

                name = csvRecord[1].split("_")[0]

                if (name == selectedExerciseName) {
                    var poseSample: PoseSample = getPoseSample(csvRecord)!!
                    poseSamples.add(poseSample)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error when loading pose samples from file.\n$e")
        }
        poseClassifier = PoseClassifier(poseSamples)
        if (isStreamMode) {
            for (className in POSE_CLASSES) {
                repCounters!!.add(RepetitionCounter(className))
            }
            Log.d(TAG,"repCounters: $repCounters")
        }

    }

    private fun getPoseSample(csvRecord:CSVRecord): PoseSample? {

        if (csvRecord.size() != NUM_LANDMARKS * NUM_DIMS + 2) {
            Log.e(TAG, "Invalid number of tokens for PoseSample")
            return null
        }

        val name = csvRecord[0]
        val className = csvRecord[1]
        val landmarks: MutableList<PointF3D> = ArrayList()

        var i =2

        while (i < csvRecord.size()){
            try {
                landmarks.add(
                    PointF3D.from(
                        csvRecord[i].toFloat(),
                        csvRecord[i + 1].toFloat(),
                        csvRecord[i + 2].toFloat()
                    )
                )
            }
            catch (e:NullPointerException){
                Log.e(TAG,"Invalide value" + csvRecord[i] + "for landmark position.")
                return null
            }catch (e: NumberFormatException) {
                Log.e(TAG, "Invalid value " + csvRecord[i] + " for landmark position.")
                return null
            }
            i += NUM_DIMS
        }
        return PoseSample(name, className, landmarks)
    }

    @WorkerThread
    fun getPoseResult(pose: Pose): List<String?>? {
        com.google.common.base.Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String?> = ArrayList()
        var classification: ClassificationResult? = poseClassifier?.classify(pose)

        // Update {@link RepetitionCounter}s if {@code isStreamMode}.
        if (isStreamMode) {
            // Feed pose to smoothing even if no pose found.
            classification = emaSmoothing?.getSmoothedResult(classification)

            // Return early without updating repCounter if no pose found.
            if (pose.allPoseLandmarks.isEmpty()) {
                result.add(lastRepResult)
                Log.d(TAG,"No Pose Found")
                return result
            }
            Log.d(TAG,"Pose found, starting repcounter...")
            for (repCounter in repCounters!!) {
                val repsBefore: Int = repCounter.getNumRepeats()
                val repsAfter: Int? = classification?.let { repCounter.addClassificationResult(it) }
                if (repsAfter != null) {
                    if (repsAfter > repsBefore) {
                        // Play a fun beep when rep counter updates.
                        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP)
                        count = repsAfter
                        lastRepResult = java.lang.String.format(
                            Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter
                        )
                        break
                    }
                }
            }
            Log.d(TAG, "lastRepResult:$lastRepResult")
            result.add(lastRepResult)
        }

        // Add maxConfidence class of current frame to result if pose is found.
        if (!pose.allPoseLandmarks.isEmpty()) {
            val maxConfidenceClass: String? = classification?.getMaxConfidenceClass()
            val maxConfidenceClassResult: String = java.lang.String.format(
                Locale.US,
                "%s : %.2f confidence",
                maxConfidenceClass, classification?.getClassConfidence(maxConfidenceClass)!!
                        / poseClassifier?.confidenceRange()!!
            )
            result.add(maxConfidenceClassResult)
        }
        return result
    }

    //return represult to a function so that processor can call from it, slowly pass it back to exercise
    fun getCounter():Int{
        return count
    }


}