package com.example.foctrainer

import com.example.foctrainer.databinding.ActivityExerciseBinding
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import java.util.*
import java.util.concurrent.ExecutorService
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Math.atan2
import kotlin.math.atan2 as kotlinMathAtan2

//pose analyzer class set up
private class PoseAnalyzer(private val poseFoundListener: (Pose) -> Unit) : ImageAnalysis.Analyzer {

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

//drawing class
class RectOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val STROKE_WIDTH = 3f // has to be float
    private val drawColor = Color.RED
    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    fun clear() {
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    internal fun drawLine(
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?
    ) {
        val start = startLandmark!!.position
        val end = endLandmark!!.position


        val xmul = 3.3f;
        val ymul = 3.3f;

        extraCanvas.drawLine(
            (start.x * xmul) - 250, start.y* ymul, (end.x* xmul) -250, end.y* ymul, paint
        )
        invalidate();
    }

    internal fun drawNeck(
        _occhioSx: PoseLandmark?,
        _occhioDx: PoseLandmark?,
        _spallaSx: PoseLandmark?,
        _spallaDx: PoseLandmark?
    ) {

        val xmul = 3.3f;
        val ymul = 3.3f;


        val occhioSx = _occhioSx!!.position
        val occhioDx = _occhioDx!!.position
        val spallaSx = _spallaSx!!.position
        val spallaDx = _spallaDx!!.position


        val fineColloX =  occhioDx.x +  ((occhioSx.x - occhioDx.x) / 2);
        val fineColloY = occhioDx.y + ((occhioSx.y - occhioDx.y) / 2);

        val inizioColloX = spallaDx.x + ((spallaSx.x - spallaDx.x ) / 2);
        val inizioColloY = spallaDx.y + ((spallaSx.y - spallaDx.y) / 2);

        extraCanvas.drawLine(
            (fineColloX * xmul) - 250, fineColloY* ymul, (inizioColloX* xmul) -250, inizioColloY* ymul, paint
        )

        extraCanvas.drawLine(
            (occhioSx.x * xmul) - 250, occhioSx.y* ymul, (occhioDx.x* xmul) -250, occhioDx.y* ymul, paint
        )
        invalidate();
    }


}


//set up camerax
class Exercise : AppCompatActivity() {

    private lateinit var binding:ActivityExerciseBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
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
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //check permission granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    //starting camera
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, PoseAnalyzer(::onTextFound))
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))


    }

    fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {

        var result = Math.toDegrees(
            kotlinMathAtan2( lastPoint.getPosition().y.toDouble() - midPoint.getPosition().y,
                lastPoint.getPosition().x.toDouble() - midPoint.getPosition().x)
                - kotlinMathAtan2(firstPoint.getPosition().y - midPoint.getPosition().y,
            firstPoint.getPosition().x - midPoint.getPosition().x)
        )
        result = Math.abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    fun getNeckAngle(
        orecchio: PoseLandmark, spalla: PoseLandmark
    ): Double {

        var result = Math.toDegrees(
            kotlinMathAtan2( spalla.getPosition().y.toDouble() - spalla.getPosition().y,
                (spalla.getPosition().x + 100 ).toDouble() - spalla.getPosition().x)
                - kotlinMathAtan2(orecchio.getPosition().y - spalla.getPosition().y,
            orecchio.getPosition().x - spalla.getPosition().x)
        )

        result = Math.abs(result) // Angle should never be negative

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

            val occhioSx = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
            val occhioDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);

            val orecchioDx = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
            val orecchioSx = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);

            val builder = StringBuilder()
            binding.rectOverlay.clear()

            if( occhioSx != null && occhioDx != null && leftShoulder != null && rightShoulder != null  ){
                binding.rectOverlay.drawNeck(occhioSx, occhioDx, leftShoulder, rightShoulder);
            }

            if(orecchioSx != null && leftShoulder != null){
                binding.rectOverlay.drawLine(orecchioSx, leftShoulder)
                var angoloCollo = getNeckAngle(orecchioSx, leftShoulder);
                builder.append("${90 - angoloCollo.toInt()} collo (da sx) \n")
            }

            if(orecchioDx != null && rightShoulder != null){
                binding.rectOverlay.drawLine(orecchioDx, rightShoulder)
                var angoloCollo = getNeckAngle(orecchioDx, rightShoulder);
                builder.append("${90 - angoloCollo.toInt()} collo (da dx) \n")
            }

            if(rightShoulder != null && rightHip != null && rightKnee != null){
                var angoloBusto = getAngle(rightShoulder, rightHip, rightKnee);
                builder.append("${ 180 - angoloBusto.toInt()} busto (da dx) \n")
            }

            if(leftShoulder != null && leftHip != null && leftKnee != null){
                var angoloBusto = getAngle(leftShoulder, leftHip, leftKnee);
                builder.append("${180 - angoloBusto.toInt()} busto (da sx) \n")
            }

            if( rightHip != null && rightKnee != null  && rightAnkle != null){
                var angoloBusto = getAngle( rightHip, rightKnee, rightAnkle);
                builder.append("${ 180 - angoloBusto.toInt()} gamba (da dx) \n")
            }

            // angolo gamba sinistra
            if( leftHip != null && leftKnee != null  && leftAnkle != null){
                var angoloBusto = getAngle( leftHip, leftKnee,leftAnkle);
                builder.append("${ 180 - angoloBusto.toInt()} gamba (da sx) \n")
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


            binding.textViewId.setText("${builder.toString()}")

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


