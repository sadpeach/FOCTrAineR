package com.example.foctrainer.exercise

import com.example.foctrainer.databinding.ActivityExerciseBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import java.util.concurrent.ExecutorService
import android.os.Build
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2 as kotlinMathAtan2
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.camera.view.PreviewView
import com.example.foctrainer.databinding.ActivityCameraxLivePreviewBinding
import com.google.mlkit.common.MlKitException


class Exercise : AppCompatActivity() {

//    private lateinit var binding:ActivityExerciseBinding
    private lateinit var binding:ActivityCameraxLivePreviewBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewView: PreviewView? = null
    private var cameraSelector: CameraSelector? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var selectedModel = OBJECT_DETECTION
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var graphicOverlay: GraphicOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityExerciseBinding.inflate(layoutInflater)
        binding = ActivityCameraxLivePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        graphicOverlay = binding.graphicOverlay
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, OBJECT_DETECTION)
        }

        if (allPermissionsGranted()) {
//            startCamera()
            cameraProvider()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
//        cameraExecutor = Executors.newSingleThreadExecutor() //camera will be running on a new thread
    }

    //grant media access permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
//                startCamera()
                cameraProvider()

            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun cameraProvider () {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(CameraXViewModel::class.java)
            .getProcessCameraProvider()
            ?.observe(
                this,
                { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    bindAllCameraUseCases()
                }
            )
    }

    //check permission granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor =
            try {
                val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                val shouldShowInFrameLikelihood =
                    PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
                val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
                val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
                val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
                PoseDetectorProcessor(
                    this,
                    poseDetectorOptions!!,
                    shouldShowInFrameLikelihood,
                    visualizeZ,
                    rescaleZ,
                    runClassification,
                    /* isStreamMode = */ true
                )

            } catch (e: Exception) {
                Log.e(TAG, "Can not create image processor: $selectedModel", e)
                Toast.makeText(
                    applicationContext,
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this),
            { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, isImageFlipped)
                    } else {
                        graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, isImageFlipped)
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    graphicOverlay?.let { imageProcessor!!.processImageProxy(imageProxy, it) }
                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        )
        Log.d(TAG,"Died here")
        if (cameraSelector == null) Log.d(TAG,"cameraSelector null")
        if (analysisUseCase == null) Log.d(TAG,"cameraSelector null")
        if (this == null) Log.d(TAG,"context null")
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, analysisUseCase)
        Log.d(TAG,"Died after binding")
    }


    private fun bindPreviewUseCase() {

        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder();
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, previewUseCase)
    }

//    //starting camera
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            imageCapture = ImageCapture.Builder()
//                .build()
//
//            // Select back camera as a default
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    //PoseAnalyzer(::onTextFound) passing onTextFound function to poseAnalyzer
//                    it.setAnalyzer(cameraExecutor, PoseAnalyzer(::onTextFound,runClassification,isStreamMode,this))
//                }
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, imageCapture, imageAnalyzer)
//
//
//            } catch(exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }


//    fun getProcessCameraProvider(): LiveData<ProcessCameraProvider?>? {
//        if (cameraProviderLiveData == null) {
//            cameraProviderLiveData = MutableLiveData()
//            val cameraProviderFuture = ProcessCameraProvider.getInstance(
//                application
//            )
//            cameraProviderFuture.addListener(
//                {
//                    try {
//                        cameraProviderLiveData.setValue(cameraProviderFuture.get())
//                    } catch (e: ExecutionException) {
//                        // Handle any errors (including cancellation) here.
//                        Log.e(TAG, "Unhandled exception", e)
//                    } catch (e: InterruptedException) {
//                        Log.e(TAG, "Unhandled exception", e)
//                    }
//                },
//                ContextCompat.getMainExecutor(application)
//            )
//        }
//        return cameraProviderLiveData
//    }

    private fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {

        var result = Math.toDegrees(
            kotlinMathAtan2( lastPoint.position.y.toDouble() - midPoint.position.y,
                lastPoint.position.x.toDouble() - midPoint.position.x)
                - kotlinMathAtan2(firstPoint.position.y - midPoint.position.y,
            firstPoint.position.x - midPoint.position.x)
        )
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    private fun getNeckAngle(
        ear: PoseLandmark, shoulder: PoseLandmark
    ): Double {

        var result = Math.toDegrees(
            kotlinMathAtan2( shoulder.position.y.toDouble() - shoulder.position.y,
                (shoulder.position.x + 100 ).toDouble() - shoulder.position.x)
                - kotlinMathAtan2(ear.position.y - shoulder.position.y,
                ear.position.x - shoulder.position.x)
        )

        result = abs(result) // Angle should never be negative

        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    //landmark
//    private fun onTextFound(pose:Pose){
//        try {
//            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
//            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
//            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
//            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
//            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
//            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
//            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
//            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
//
//            val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
//            val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
//            val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
//            val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
//            val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
//            val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
//            val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
//            val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
//            val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
//            val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
//
//            val eyeSx = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
//            val eyeDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
//
//            val earDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
//            val earSx = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
//
//            val builder = StringBuilder()
//            binding.rectOverlay.clear()
//
//            if( eyeSx != null && eyeDx != null && leftShoulder != null && rightShoulder != null  ){
//                binding.rectOverlay.drawNeck(eyeSx, eyeDx, leftShoulder, rightShoulder)
//            }
//
//            if(earSx != null && leftShoulder != null){
//                binding.rectOverlay.drawLine(earSx, leftShoulder)
//                val neckAngle = getNeckAngle(earSx, leftShoulder)
//                builder.append("${90 - neckAngle.toInt()} collo (da sx) \n")
//            }
//
//            if(earDx != null && rightShoulder != null){
//                binding.rectOverlay.drawLine(earDx, rightShoulder)
//                val neckAngle = getNeckAngle(earDx, rightShoulder)
//                builder.append("${90 - neckAngle.toInt()} collo (da dx) \n")
//            }
//
//            if(rightShoulder != null && rightHip != null && rightKnee != null){
//                val neckAngle = getAngle(rightShoulder, rightHip, rightKnee)
//                builder.append("${ 180 - neckAngle.toInt()} busto (da dx) \n")
//            }
//
//            if(leftShoulder != null && leftHip != null && leftKnee != null){
//                val neckAngle = getAngle(leftShoulder, leftHip, leftKnee)
//                builder.append("${180 - neckAngle.toInt()} busto (da sx) \n")
//            }
//
//            if( rightHip != null && rightKnee != null  && rightAnkle != null){
//                val neckAngle = getAngle( rightHip, rightKnee, rightAnkle)
//                builder.append("${ 180 - neckAngle.toInt()} gamba (da dx) \n")
//            }
//
//            // angolo gamba sinistra
//            if( leftHip != null && leftKnee != null  && leftAnkle != null){
//                val neckAngle = getAngle( leftHip, leftKnee,leftAnkle)
//                builder.append("${ 180 - neckAngle.toInt()} gamba (da sx) \n")
//            }
//
//
//            if(leftShoulder != null && rightShoulder != null){
//                binding.rectOverlay.drawLine(leftShoulder, rightShoulder)
//            }
//
//            if(leftHip != null &&  rightHip != null){
//                binding.rectOverlay.drawLine(leftHip, rightHip)
//            }
//
//            if(leftShoulder != null &&  leftElbow != null){
//                binding.rectOverlay.drawLine(leftShoulder, leftElbow)
//            }
//
//            if(leftElbow != null &&  leftWrist != null){
//                binding.rectOverlay.drawLine(leftElbow, leftWrist)
//            }
//
//            if(leftShoulder != null &&  leftHip != null){
//                binding.rectOverlay.drawLine(leftShoulder, leftHip)
//            }
//
//            if(leftHip != null &&  leftKnee != null){
//                binding.rectOverlay.drawLine(leftHip, leftKnee)
//            }
//
//            if(leftKnee != null &&  leftAnkle != null){
//                binding.rectOverlay.drawLine(leftKnee, leftAnkle)
//            }
//
//            if(leftWrist != null &&  leftThumb != null){
//                binding.rectOverlay.drawLine(leftWrist, leftThumb)
//            }
//
//            if(leftWrist != null &&  leftPinky != null){
//                binding.rectOverlay.drawLine(leftWrist, leftPinky)
//            }
//
//            if(leftWrist != null &&  leftIndex != null){
//                binding.rectOverlay.drawLine(leftWrist, leftIndex)
//            }
//
//            if(leftIndex != null &&  leftPinky != null){
//                binding.rectOverlay.drawLine(leftIndex, leftPinky)
//            }
//
//            if(leftAnkle != null &&  leftHeel != null){
//                binding.rectOverlay.drawLine(leftAnkle, leftHeel)
//            }
//
//            if(leftHeel != null &&  leftFootIndex != null){
//                binding.rectOverlay.drawLine(leftHeel, leftFootIndex)
//            }
//
//            if(rightShoulder != null &&  rightElbow != null){
//                binding.rectOverlay.drawLine(rightShoulder, rightElbow)
//            }
//
//            if(rightElbow != null &&  rightWrist != null){
//                binding.rectOverlay.drawLine(rightElbow, rightWrist)
//            }
//
//            if(rightShoulder != null &&  rightHip != null){
//                binding.rectOverlay.drawLine(rightShoulder, rightHip)
//            }
//
//            if(rightHip != null &&  rightKnee != null){
//                binding.rectOverlay.drawLine(rightHip, rightKnee)
//            }
//
//            if(rightKnee != null &&  rightAnkle != null){
//                binding.rectOverlay.drawLine(rightKnee, rightAnkle)
//            }
//
//            if(rightWrist != null &&  rightThumb != null){
//                binding.rectOverlay.drawLine(rightWrist, rightThumb)
//            }
//
//            if(rightWrist != null &&  rightPinky != null){
//                binding.rectOverlay.drawLine(rightWrist, rightPinky)
//            }
//
//            if(rightWrist != null &&  rightIndex != null){
//                binding.rectOverlay.drawLine(rightWrist, rightIndex)
//            }
//
//            if(rightIndex != null &&  rightPinky != null){
//                binding.rectOverlay.drawLine(rightIndex, rightPinky)
//            }
//
//            if(rightAnkle != null &&  rightHeel != null){
//                binding.rectOverlay.drawLine(rightAnkle, rightHeel)
//            }
//
//            if(rightHeel != null &&  rightFootIndex != null){
//                binding.rectOverlay.drawLine(rightHeel, rightFootIndex)
//            }
//
//
//            binding.textViewId.text = "$builder"
//
//        } catch (e: java.lang.Exception) {
//            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
//        }
//    }

    //end camera activity
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }

    //other details
    companion object {
        const val TAG = "ExerciseLog"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val POSE_DETECTION = "Pose Detection"
        private const val STATE_SELECTED_MODEL = "selected_model"
        private const val OBJECT_DETECTION = "Object Detection"
        private const val SELFIE_SEGMENTATION = "Selfie Segmentation"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}


