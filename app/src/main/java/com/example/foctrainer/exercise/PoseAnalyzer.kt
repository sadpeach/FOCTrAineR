package com.example.foctrainer.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.Executor;
import java.util.concurrent.Executors

/**
 * Purpose: Calculate rotation value
 *
 * **/
class PoseAnalyzer(private val poseFoundListener: (Pose) -> Unit, private val runClassification: Boolean, private val isStreamMode:Boolean,private val context: Context) : ImageAnalysis.Analyzer { //takes lambda as 2nd argument
    private val TAG ="PostAnalyzer"
    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)
    private var classificationExecutor: Executor = Executors.newSingleThreadExecutor();
    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /** Internal class to hold Pose and classification results. */
    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //passing image to MLKit API for processing the image
            poseDetector
                .process(image)
                .continueWith(classificationExecutor, {task ->
                    Log.d(TAG,"attempting to process image")
                    val pose = task.result
                    poseFoundListener(pose)
                    var classificationResult: List<String> = ArrayList()
                    if (runClassification){
                        if (poseClassifierProcessor == null) {
                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
                        }
                        classificationResult = poseClassifierProcessor!!.getPoseResult(pose) as List<String>
                    }
                    PoseWithClassification(pose, classificationResult)
                    imageProxy.close()
                }
                    )
//                .addOnSuccessListener { pose -> //task completed successfully
//                    poseFoundListener(pose)
//
//                    imageProxy.close()
//                }
                .addOnFailureListener { error -> //task failed with exception
                    Log.d(TAG, "Failed to process the image")
                    error.printStackTrace()
                    imageProxy.close()
                }
        }
    }





}