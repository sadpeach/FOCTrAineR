package com.example.foctrainer.exercise

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.GuardedBy
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.gms.tasks.Tasks
import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.odml.image.MlImage
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import java.lang.Math.max
import java.lang.Math.min
import java.nio.ByteBuffer
import java.util.Timer
import java.util.TimerTask

abstract class VisionProcessorBase<T>(context: Context) : VisionImageProcessor {

    companion object {
        const val MANUAL_TESTING_LOG = "LogTagForTest"
        private const val TAG = "x"
    }

    private var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val fpsTimer = Timer()
    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    // Used to calculate latency, running in the same thread, no sync needed.
    private var numRuns = 0
    private var totalFrameMs = 0L
    private var maxFrameMs = 0L
    private var minFrameMs = Long.MAX_VALUE
    private var totalDetectorMs = 0L
    private var maxDetectorMs = 0L
    private var minDetectorMs = Long.MAX_VALUE
    private var exerciseCounter = 199


    // Frame count that have been processed so far in an one second interval to calculate FPS.
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    // To keep the latest images and its metadata.
    @GuardedBy("this") private var latestImage: ByteBuffer? = null
    @GuardedBy("this") private var latestImageMetaData: FrameMetadata? = null
    // To keep the images and metadata in process.
    @GuardedBy("this") private var processingImage: ByteBuffer? = null
    @GuardedBy("this") private var processingMetaData: FrameMetadata? = null

    init {
        Log.d(TAG,"start processing @ constructor")
        fpsTimer.scheduleAtFixedRate(

            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    @ExperimentalGetImage
    override fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay) {
        val frameStartMs = SystemClock.elapsedRealtime()
        if (isShutdown) {
            return
        }
        var bitmap: Bitmap? = null
        if (!PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.context)) {
            bitmap = BitmapUtils.getBitmap(image)
        }

        if (isMlImageEnabled(graphicOverlay.context)) {
            Log.d(TAG,"start processing graphicoverlay")
            val mlImage =
                MediaMlImageBuilder(image.image!!).setRotation(image.imageInfo.rotationDegrees).build()
            requestDetectInImage(
                mlImage,
                graphicOverlay,
                /* originalCameraImage= */ bitmap,
                /* shouldShowFps= */ true,
                frameStartMs
            )

                .addOnCompleteListener { image.close() }

            return
        }

        requestDetectInImage(
            InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees),
            graphicOverlay,
            /* originalCameraImage= */ bitmap,
            /* shouldShowFps= */ true,
            frameStartMs
        )

            .addOnCompleteListener { image.close() }
    }

    // -----------------Common processing logic-------------------------------------------------------
    private fun requestDetectInImage(
        image: InputImage,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        shouldShowFps: Boolean,
        frameStartMs: Long
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay,
            originalCameraImage,
            shouldShowFps,
            frameStartMs
        )
    }

    private fun requestDetectInImage(
        image: MlImage,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        shouldShowFps: Boolean,
        frameStartMs: Long
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay,
            originalCameraImage,
            shouldShowFps,
            frameStartMs
        )
    }

    private fun setUpListener(
        task: Task<T>,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        shouldShowFps: Boolean,
        frameStartMs: Long
    ): Task<T> {
        val detectorStartMs = SystemClock.elapsedRealtime()
        return task
            .addOnSuccessListener(
                executor,
                OnSuccessListener { results: T ->
                    val endMs = SystemClock.elapsedRealtime()
                    val currentFrameLatencyMs = endMs - frameStartMs
                    val currentDetectorLatencyMs = endMs - detectorStartMs
                    if (numRuns >= 500) {
                        resetLatencyStats()
                    }
                    numRuns++
                    frameProcessedInOneSecondInterval++
                    totalFrameMs += currentFrameLatencyMs
                    maxFrameMs = max(currentFrameLatencyMs, maxFrameMs)
                    minFrameMs = min(currentFrameLatencyMs, minFrameMs)
                    totalDetectorMs += currentDetectorLatencyMs
                    maxDetectorMs = max(currentDetectorLatencyMs, maxDetectorMs)
                    minDetectorMs = min(currentDetectorLatencyMs, minDetectorMs)

                    // Only log inference info once per second. When frameProcessedInOneSecondInterval is
                    // equal to 1, it means this is the first frame processed during the current second.
                    if (frameProcessedInOneSecondInterval == 1) {
                        Log.d(TAG, "Num of Runs: $numRuns")
                        Log.d(
                            TAG,
                            "Frame latency: max=" +
                                    maxFrameMs +
                                    ", min=" +
                                    minFrameMs +
                                    ", avg=" +
                                    totalFrameMs / numRuns
                        )
                        Log.d(
                            TAG,
                            "Detector latency: max=" +
                                    maxDetectorMs +
                                    ", min=" +
                                    minDetectorMs +
                                    ", avg=" +
                                    totalDetectorMs / numRuns
                        )
                        val mi = ActivityManager.MemoryInfo()
                        activityManager.getMemoryInfo(mi)
                        val availableMegs: Long = mi.availMem / 0x100000L
                        Log.d(TAG, "Memory available in system: $availableMegs MB")
                    }
                    graphicOverlay.clear()
                    if (originalCameraImage != null) {
                        graphicOverlay.add(CameraImageGraphic(graphicOverlay, originalCameraImage))
                    }
                    this@VisionProcessorBase.onSuccess(results, graphicOverlay)
                    if (!PreferenceUtils.shouldHideDetectionInfo(graphicOverlay.context)) {
                        graphicOverlay.add(
                            InferenceInfoGraphic(
                                graphicOverlay,
                                currentFrameLatencyMs,
                                currentDetectorLatencyMs,
                                if (shouldShowFps) framesPerSecond else null
                            )
                        )
                    }
                    graphicOverlay.postInvalidate()
                }
            )
            .addOnFailureListener(
                executor,
                OnFailureListener { e: Exception ->
                    graphicOverlay.clear()
                    graphicOverlay.postInvalidate()
                    val error = "Failed to process. Error: " + e.localizedMessage
                    Toast.makeText(
                        graphicOverlay.context,
                        """
          $error
          Cause: ${e.cause}
          """.trimIndent(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d(TAG, error)
                    e.printStackTrace()
                    this@VisionProcessorBase.onFailure(e)
                }
            )
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
        resetLatencyStats()
        fpsTimer.cancel()
    }

    private fun resetLatencyStats() {
        numRuns = 0
        totalFrameMs = 0
        maxFrameMs = 0
        minFrameMs = Long.MAX_VALUE
        totalDetectorMs = 0
        maxDetectorMs = 0
        minDetectorMs = Long.MAX_VALUE
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    protected open fun detectInImage(image: MlImage): Task<T> {
        return Tasks.forException(
            MlKitException(
                "MlImage is currently not demonstrated for this feature",
                MlKitException.INVALID_ARGUMENT
            )
        )
    }

    override fun getCounter():Int{
        return exerciseCounter
    }
    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)

    protected abstract fun onFailure(e: Exception)

    protected open fun isMlImageEnabled(context: Context?): Boolean {
        return false
    }

}