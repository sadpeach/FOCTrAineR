package com.example.foctrainer.exercise

import android.content.Context
import android.media.ToneGenerator

import android.media.AudioManager

import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
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


class PoseClassifierProcessor {
    private val TAG = "PoseClassifierProcessor"
    private val POSE_SAMPLES_FILE = "pose_samples.csv"

    private val NUM_LANDMARKS = 33
    private val NUM_DIMS = 3

    // Specify classes for which we want rep counting.
    // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
    // for your pose samples.
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

    @WorkerThread
    constructor(context: Context, isStreamMode: Boolean,selectedExerciseId:Int) {
        Log.d(TAG,"starting configuration for poseClassifierProcessor...")
        //precondition -> throws illegalArgument
        //return the app's main looper
        com.google.common.base.Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        this.isStreamMode = isStreamMode
        if (isStreamMode) {
            emaSmoothing = EMASmoothing()
            repCounters = ArrayList()
            lastRepResult = ""
        }
        this.selectedExerciseId = selectedExerciseId
        loadPoseSamples(context,selectedExerciseId)
        Log.d(TAG,"completed configuration for poseClassifierProcessor..")
    }

    /**
     * Reads pose samples from the CSV file to pose object and add it to arrayList
     * Instantiate RepetitionCounter
     * Instantiate PoseClassifier
     *
     * @param context
     * @return void
     */
    private fun loadPoseSamples(context: Context,selectedExerciseId: Int) {
        val poseSamples: MutableList<PoseSample?> = ArrayList()

        //load from csv
        try {
            Log.d(TAG, "starting to load samples from file.\n")
            val reader = BufferedReader(InputStreamReader(context.assets.open(POSE_SAMPLES_FILE)))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT)

            for (csvRecord in csvParser){

                var poseSample : PoseSample = getPoseSample(csvRecord)!!
                poseSamples.add(poseSample)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error when loading pose samples from file.\n$e")
        }
        Log.d(TAG,"poseSamples: ${poseSamples.size}")
        Log.d(TAG,"poseSamples name: ${poseSamples[1]?.getClassName()}")
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
        Log.d(TAG,"className retrieved:"+className)
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

    /**
     * Given a new [Pose] input, returns a list of formatted [String]s with Pose
     * classification results.
     *
     *
     * Currently it returns up to 2 strings as following:
     * 0: PoseClass : X reps
     * 1: PoseClass : [0.0-1.0] confidence
     */
    @WorkerThread
    fun getPoseResult(pose: Pose): List<String?>? {
        Log.d(TAG,"getting pose result...")
        com.google.common.base.Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String?> = ArrayList()
        var classification: ClassificationResult? = poseClassifier?.classify(pose)

        Log.d(TAG,"getting pose result -> classification: $classification")

        // Update {@link RepetitionCounter}s if {@code isStreamMode}.
        if (isStreamMode) {
            Log.d(TAG,"stream mode set")
            // Feed pose to smoothing even if no pose found.
            classification = emaSmoothing?.getSmoothedResult(classification)
            Log.d(TAG,"smoothing classification" + classification)
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
                        lastRepResult = java.lang.String.format(
                            Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter
                        )
                        break
                    }
                }
            }
            Log.d(TAG,"lastRepResult:"+lastRepResult)
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



}