package com.example.foctrainer.exercise

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class PoseAnalyzer(private val poseFoundListener: (Pose) -> Unit) : ImageAnalysis.Analyzer {

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            poseDetector
                .process(image)
                .addOnSuccessListener { pose ->
                    poseFoundListener(pose)
                    imageProxy.close()
                }
                .addOnFailureListener { error ->
                    Log.d(Exercise.TAG, "Failed to process the image")
                    error.printStackTrace()
                    imageProxy.close()
                }
        }
    }
}