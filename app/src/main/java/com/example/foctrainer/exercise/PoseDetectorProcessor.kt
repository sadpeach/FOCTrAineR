package com.example.foctrainer.exercise

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.pose.Pose

import com.google.android.odml.image.MlImage

import com.google.mlkit.vision.common.InputImage

import com.google.mlkit.vision.pose.PoseDetection

import com.google.mlkit.vision.pose.PoseDetectorOptionsBase

import com.google.mlkit.vision.pose.PoseDetector
import java.lang.Exception
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.ArrayList


/** A processor to run pose detector. */
class PoseDetectorProcessor(
    private val context: Context,
    options: PoseDetectorOptionsBase,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val runClassification: Boolean,
    private val isStreamMode: Boolean
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor

    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /** Internal class to hold Pose and classification results. */
    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)

    init {
        classificationExecutor = Executors.newSingleThreadExecutor()
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<PoseWithClassification> {
        Log.d(TAG, "processing image detection, runClassification$runClassification")
        return detector
            .process(image)
            .continueWith(
                classificationExecutor,
                { task ->
                    val pose = task.getResult()
                    var classificationResult: List<String> = ArrayList()
                    if (runClassification) {
                        Log.d(TAG,"running classification with inputImg")
                        if (poseClassifierProcessor == null) {
                            Log.d(TAG,"poseClassifierProcessor is null")
                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
                        }
                        classificationResult = poseClassifierProcessor!!.getPoseResult(pose) as List<String>
                        Log.d(TAG,"classificationResult"+classificationResult)
                    }
                    PoseWithClassification(pose, classificationResult)
                }
            )
    }

    override fun detectInImage(image: MlImage): Task<PoseWithClassification> {
        Log.d(TAG, "processing MLimage detection, runClassification $runClassification")

        return detector
            .process(image)
            .continueWith(
                classificationExecutor,
                { task ->
                    val pose = task.getResult()
                    Log.d(TAG, "task.getresult: ${pose.getPoseLandmark(1)}")
                    var classificationResult: List<String> = ArrayList()
                    Log.d(TAG,"running classification with MLimage..")

                    if (runClassification) {
                        Log.d(TAG,"runClassification enabled")
                        if (poseClassifierProcessor == null) {
                            Log.d(TAG,"setting poseClassifierProcessor")
                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
                            Log.d(TAG, "poseClassifierProcessor setting completed $poseClassifierProcessor")
                        }
                        classificationResult = poseClassifierProcessor?.getPoseResult(pose) as List<String>

                    }
                    Log.d(TAG,"ending classification " + classificationResult)
                    PoseWithClassification(pose, classificationResult)
                }
            )
    }

    override fun onSuccess(

        poseWithClassification: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        Log.d(TAG,"onsuccess")
        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                poseWithClassification.pose,
                showInFrameLikelihood,
                visualizeZ,
                rescaleZForVisualization,
                poseWithClassification.classificationResult
            )
        )
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    override fun processBitmap(bitmap: Bitmap, graphicOverlay: GraphicOverlay) {
        TODO("Not yet implemented")
    }

    companion object {
        private val TAG = "PoseDetectorProcessor"
    }
}