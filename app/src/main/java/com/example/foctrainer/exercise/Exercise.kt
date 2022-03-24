package com.example.foctrainer.exercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import android.os.Build
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityCameraxLivePreviewBinding
import com.google.mlkit.common.MlKitException
import kotlinx.coroutines.*
import com.example.foctrainer.MainActivity
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.utils.CaloriesCalculator
import com.example.foctrainer.viewModel.*
import java.text.SimpleDateFormat
import java.util.*


class Exercise : AppCompatActivity()  {

    private lateinit var binding:ActivityCameraxLivePreviewBinding
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
    private var selectedExerciseId: Int = 0
    private var scheduleId: Int = 0
    private val userId = 1 // need to change

    //all database
    private val exerciseViewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory((application as FocTrainerApplication).exerciseRepository)
    }

    private val completeExerciseModel: CompletedExerciseViewModel by viewModels {
        CompletedExerciseViewModelFactory((application as FocTrainerApplication).completedExerciseRepository)
    }

    private val scheduleModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxLivePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get exerciseId
        selectedExerciseId = intent.getIntExtra("exerciseId",0)
        Log.d(TAG, "Selected Exercise Id: $selectedExerciseId")

        //get scheduleId if any
        scheduleId = intent.getIntExtra("scheduleId",0)
        Log.d(TAG,"Selected Schedule ID: $scheduleId")

        //get exerciseName
        exerciseViewModel.getExerciseNameById(selectedExerciseId).observe(this, { exerciseName ->
            title = exerciseName
            Log.d(TAG,"Retrieved exercise name from DB: $exerciseName")
        })

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        graphicOverlay = binding.graphicOverlay
        previewView = binding.previewView

        if (previewView == null) {
            Log.d(TAG, "previewView is null")
        }

        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, OBJECT_DETECTION)
        }

        //permission check
        if (allPermissionsGranted()) {
            cameraProvider()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        if (scheduleId != 0) getScheduledWorkOutCount()

        binding.endWorkout.setOnClickListener() {
            val exeName = title.toString()
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Workout Ending")
                .setMessage("You have completed ${binding.counter.text} $title")
                .setPositiveButton("YES") { dialog, whichButton ->
                    Log.d(TAG,"Saving workout result to database")
                    val counter = binding.counter.text.toString().toInt()
                    val date = getCurrentDateTime()
                    val dateTime = date.toString("yyyy-MM-dd HH:mm")

                    val completedExercise = CompletedExerciseModel(
                        userId = userId,
                        exerciseId = selectedExerciseId,
                        completed_dateTime = dateTime,
                        no_completed_sets = counter,
                        total_calories = CaloriesCalculator(
                            weight = 12.3f,
                            exerciseName = exeName,
                            noOfExerciseCompleted = counter
                        ).getCalories())
                    completeExerciseModel.insertNewCompletedExercise(completedExercise = completedExercise )

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(applicationContext, "Workout Result Saved", Toast.LENGTH_SHORT).show()

                }
                .setNegativeButton("CANCEL") { dialog, whichButton ->
                    dialog.dismiss()
                }

            dialog.show()
        }

//        Log.d(TAG,"count:${binding.counter.text}, scheduleGoal:${binding.scheduledGoal.text}")
//        if (binding.counter.text.toString().toInt() == binding.scheduledGoal.text.toString().toInt()){
//            val popdialog = AlertDialog.Builder(this)
//            popdialog.setTitle("Congratulations")
//                .setMessage("Scheduled workout event completed!")
//
//            popdialog.show()
//
//            val t = Timer()
//            t.schedule(object : TimerTask() {
//                override fun run() {
//                    popdialog.setCancelable(true)
//                    t.cancel()
//                }
//            }, 2000)
//        }

    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(STATE_SELECTED_MODEL, selectedModel)
    }

    //grant media access permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
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
        ViewModelProvider(this)
            .get(CameraXViewModel::class.java)
            .getProcessCameraProvider()
            ?.observe(
                this,
                {
                    cameraProvider = it
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
                val shouldShowInFrameLikelihood = false
                val visualizeZ = true
                val rescaleZ = true
                val runClassification = true
//                val selectedExerciseName = title as String
                val selectedExerciseName = title.toString()

                PoseDetectorProcessor(
                    this,
                    poseDetectorOptions!!,
                    shouldShowInFrameLikelihood,
                    visualizeZ,
                    rescaleZ,
                    runClassification,
                    /* isStreamMode = */ true,
                    selectedExerciseId ,
                    selectedExerciseName
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
                    Log.d(TAG," updating counter to: "+imageProcessor?.getCounter())
                    binding.counter.text = imageProcessor?.getCounter().toString()

                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }

        )
        if (cameraSelector == null) Log.d(TAG,"cameraSelector null")
        if (analysisUseCase == null) Log.d(TAG,"cameraSelector null")
        if (this == null) Log.d(TAG,"context null")
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, analysisUseCase)
    }

    private fun bindPreviewUseCase() {

        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder();
//        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution)
//        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider?.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, previewUseCase)
    }

    //other details
    companion object {
        const val TAG = "ExerciseLog"
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

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()

        imageProcessor?.run { this.stop() }
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun getScheduledWorkOutCount(){
        scheduleModel.getScheduledCountById(scheduleId).observe(this,{ scheduledCount ->
            binding.scheduledGoal.text = scheduledCount.toString()
        })
    }
}



//toggle between front and back camera
//    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
//        Log.d(TAG,"on checkedchanged")
//        if (cameraProvider == null) {
//            return
//        }
//        val newLensFacing =
//            if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
//                CameraSelector.LENS_FACING_BACK
//            } else {
//                CameraSelector.LENS_FACING_FRONT
//            }
//        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
//        try {
//            if (cameraProvider!!.hasCamera(newCameraSelector)) {
//                Log.d(TAG, "Set facing to $newLensFacing")
//                lensFacing = newLensFacing
//                cameraSelector = newCameraSelector
//                bindAllCameraUseCases()
//                return
//            }
//        } catch (e: CameraInfoUnavailableException) {
//            // Falls through
//        }
//        Toast.makeText(
//            applicationContext,
//            "This device does not have lens with facing: $newLensFacing",
//            Toast.LENGTH_SHORT
//        )
//            .show()
//    }


