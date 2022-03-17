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
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutionException


class Exercise : AppCompatActivity() {

    private lateinit var binding:ActivityExerciseBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val runClassification: Boolean = true // change runclassificatioin
    private val isStreamMode: Boolean = true //change isStreamMode
    private lateinit var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
//            startCamera()
            cameraProvider()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
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
        }
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


    fun getProcessCameraProvider(): LiveData<ProcessCameraProvider?>? {
        if (cameraProviderLiveData == null) {
            cameraProviderLiveData = MutableLiveData()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(
                application
            )
            cameraProviderFuture.addListener(
                {
                    try {
                        cameraProviderLiveData.setValue(cameraProviderFuture.get())
                    } catch (e: ExecutionException) {
                        // Handle any errors (including cancellation) here.
                        Log.e(TAG, "Unhandled exception", e)
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Unhandled exception", e)
                    }
                },
                ContextCompat.getMainExecutor(application)
            )
        }
        return cameraProviderLiveData
    }

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
    private fun onTextFound(pose:Pose){
        try {
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
            val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
            val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
            val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
            val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
            val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
            val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
            val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
            val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

            val eyeSx = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
            val eyeDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)

            val earDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
            val earSx = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)

            val builder = StringBuilder()
            binding.rectOverlay.clear()

            if( eyeSx != null && eyeDx != null && leftShoulder != null && rightShoulder != null  ){
                binding.rectOverlay.drawNeck(eyeSx, eyeDx, leftShoulder, rightShoulder)
            }

            if(earSx != null && leftShoulder != null){
                binding.rectOverlay.drawLine(earSx, leftShoulder)
                val neckAngle = getNeckAngle(earSx, leftShoulder)
                builder.append("${90 - neckAngle.toInt()} collo (da sx) \n")
            }

            if(earDx != null && rightShoulder != null){
                binding.rectOverlay.drawLine(earDx, rightShoulder)
                val neckAngle = getNeckAngle(earDx, rightShoulder)
                builder.append("${90 - neckAngle.toInt()} collo (da dx) \n")
            }

            if(rightShoulder != null && rightHip != null && rightKnee != null){
                val neckAngle = getAngle(rightShoulder, rightHip, rightKnee)
                builder.append("${ 180 - neckAngle.toInt()} busto (da dx) \n")
            }

            if(leftShoulder != null && leftHip != null && leftKnee != null){
                val neckAngle = getAngle(leftShoulder, leftHip, leftKnee)
                builder.append("${180 - neckAngle.toInt()} busto (da sx) \n")
            }

            if( rightHip != null && rightKnee != null  && rightAnkle != null){
                val neckAngle = getAngle( rightHip, rightKnee, rightAnkle)
                builder.append("${ 180 - neckAngle.toInt()} gamba (da dx) \n")
            }

            // angolo gamba sinistra
            if( leftHip != null && leftKnee != null  && leftAnkle != null){
                val neckAngle = getAngle( leftHip, leftKnee,leftAnkle)
                builder.append("${ 180 - neckAngle.toInt()} gamba (da sx) \n")
            }


            if(leftShoulder != null && rightShoulder != null){
                binding.rectOverlay.drawLine(leftShoulder, rightShoulder)
            }

            if(leftHip != null &&  rightHip != null){
                binding.rectOverlay.drawLine(leftHip, rightHip)
            }

            if(leftShoulder != null &&  leftElbow != null){
                binding.rectOverlay.drawLine(leftShoulder, leftElbow)
            }

            if(leftElbow != null &&  leftWrist != null){
                binding.rectOverlay.drawLine(leftElbow, leftWrist)
            }

            if(leftShoulder != null &&  leftHip != null){
                binding.rectOverlay.drawLine(leftShoulder, leftHip)
            }

            if(leftHip != null &&  leftKnee != null){
                binding.rectOverlay.drawLine(leftHip, leftKnee)
            }

            if(leftKnee != null &&  leftAnkle != null){
                binding.rectOverlay.drawLine(leftKnee, leftAnkle)
            }

            if(leftWrist != null &&  leftThumb != null){
                binding.rectOverlay.drawLine(leftWrist, leftThumb)
            }

            if(leftWrist != null &&  leftPinky != null){
                binding.rectOverlay.drawLine(leftWrist, leftPinky)
            }

            if(leftWrist != null &&  leftIndex != null){
                binding.rectOverlay.drawLine(leftWrist, leftIndex)
            }

            if(leftIndex != null &&  leftPinky != null){
                binding.rectOverlay.drawLine(leftIndex, leftPinky)
            }

            if(leftAnkle != null &&  leftHeel != null){
                binding.rectOverlay.drawLine(leftAnkle, leftHeel)
            }

            if(leftHeel != null &&  leftFootIndex != null){
                binding.rectOverlay.drawLine(leftHeel, leftFootIndex)
            }

            if(rightShoulder != null &&  rightElbow != null){
                binding.rectOverlay.drawLine(rightShoulder, rightElbow)
            }

            if(rightElbow != null &&  rightWrist != null){
                binding.rectOverlay.drawLine(rightElbow, rightWrist)
            }

            if(rightShoulder != null &&  rightHip != null){
                binding.rectOverlay.drawLine(rightShoulder, rightHip)
            }

            if(rightHip != null &&  rightKnee != null){
                binding.rectOverlay.drawLine(rightHip, rightKnee)
            }

            if(rightKnee != null &&  rightAnkle != null){
                binding.rectOverlay.drawLine(rightKnee, rightAnkle)
            }

            if(rightWrist != null &&  rightThumb != null){
                binding.rectOverlay.drawLine(rightWrist, rightThumb)
            }

            if(rightWrist != null &&  rightPinky != null){
                binding.rectOverlay.drawLine(rightWrist, rightPinky)
            }

            if(rightWrist != null &&  rightIndex != null){
                binding.rectOverlay.drawLine(rightWrist, rightIndex)
            }

            if(rightIndex != null &&  rightPinky != null){
                binding.rectOverlay.drawLine(rightIndex, rightPinky)
            }

            if(rightAnkle != null &&  rightHeel != null){
                binding.rectOverlay.drawLine(rightAnkle, rightHeel)
            }

            if(rightHeel != null &&  rightFootIndex != null){
                binding.rectOverlay.drawLine(rightHeel, rightFootIndex)
            }


            binding.textViewId.text = "$builder"

        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    //end camera activity
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    //other details
    companion object {
        const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
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


