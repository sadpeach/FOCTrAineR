//package com.example.foctrainer.exercise
//
//import android.content.Context
//import android.util.Log
//import androidx.annotation.NonNull
//import com.google.android.gms.tasks.Continuation
//import com.google.android.gms.tasks.Task
//import com.google.mlkit.vision.pose.Pose
//
//import com.google.android.odml.image.MlImage
//
//import com.google.mlkit.vision.common.InputImage
//
//import com.google.mlkit.vision.pose.PoseDetection
//
//import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
//
//import com.google.mlkit.vision.pose.PoseDetector
//import java.lang.Exception
//import java.util.concurrent.Executor
//import java.util.concurrent.Executors
//
//
///** A processor to run pose detector. */
//class PoseDetectorProcessor( private val context: Context)  {
//
//    // setting up variables
//    private val showInFrameLikelihood: Boolean = false
//    private val visualizeZ: Boolean = false
//    private val rescaleZForVisualization: Boolean = false
//    private val runClassification: Boolean = false
//    private val isStreamMode: Boolean = true
//
//    private val classificationExecutor: Executor
//    private var poseClassifierProcessor: PoseClassifierProcessor? = null
//
//
//    /** Internal class to hold Pose and classification results. */
//    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)
//
//    init {
//        classificationExecutor = Executors.newSingleThreadExecutor()
//    }
//
////    override fun stop() {
////        super.stop()
////        detector.close()
////    }
//
//     fun detectInImage(image: InputImage): Task<PoseWithClassification> {
//        return detector
//            .process(image)
//            .continueWith(
//                classificationExecutor,
//                { task ->
//                    val pose = task.getResult()
//                    var classificationResult: List<String> = ArrayList()
//                    if (runClassification) {
//                        if (poseClassifierProcessor == null) {
//                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
//                        }
//                        classificationResult = poseClassifierProcessor!!.getPoseResult(pose) as List<String>
//                    }
//                    PoseWithClassification(pose, classificationResult)
//                }
//            )
//    }
//
//     fun detectInImage(image: MlImage): Task<PoseWithClassification> {
//        return detector
//            .process(image)
//            .continueWith(
//                classificationExecutor,
//                { task ->
//                    val pose = task.getResult()
//                    var classificationResult: List<String> = ArrayList()
//                    if (runClassification) {
//                        if (poseClassifierProcessor == null) {
//                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
//                        }
//                        classificationResult = poseClassifierProcessor!!.getPoseResult(pose) as List<String>
//                    }
//                    PoseWithClassification(pose, classificationResult)
//                }
//            )
//    }
//
//    fun onSuccess(
//        poseWithClassification: PoseWithClassification,
//        graphicOverlay: GraphicOverlay
//    ) {
//        graphicOverlay.add(
//            PoseGraphic(
//                graphicOverlay,
//                poseWithClassification.pose,
//                showInFrameLikelihood,
//                visualizeZ,
//                rescaleZForVisualization,
//                poseWithClassification.classificationResult
//            )
//        )
//    }
//
//    fun onFailure(e: Exception) {
//        Log.e(TAG, "Pose detection failed!", e)
//    }
//
//    fun isMlImageEnabled(context: Context?): Boolean {
//        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
//        return true
//    }
//
//    companion object {
//        private val TAG = "PoseDetectorProcessor"
//    }
//}