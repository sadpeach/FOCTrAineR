package com.example.foctrainer.exercise

import android.os.SystemClock
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import kotlin.collections.HashSet


class EMASmoothing {

    private val DEFAULT_WINDOW_SIZE = 10
    private val DEFAULT_ALPHA = 0.2f

    private val RESET_THRESHOLD_MS: Long = 100

    private var windowSize = 0
    private var alpha = 0f

    // This is a window of {@link ClassificationResult}s as outputted by the {@link PoseClassifier}.
    // We run smoothing over this window of size {@link windowSize}.
    private var window: Deque<ClassificationResult>? = null

    private var lastInputMs: Long = 0

    constructor() : this(10, .2f) {

    }
    constructor(windowSize: Int, alpha: Float) {
        this.windowSize = windowSize
        this.alpha = alpha
        window = LinkedBlockingDeque(windowSize)
    }

    fun getSmoothedResult(classificationResult: ClassificationResult?): ClassificationResult? {
        // Resets memory if the input is too far away from the previous one in time.
        val nowMs = SystemClock.elapsedRealtime()
        if (nowMs - lastInputMs > RESET_THRESHOLD_MS) {
            window?.clear()
        }
        lastInputMs = nowMs

        // If we are at window size, remove the last (oldest) result.
        if (window!!.size == windowSize) {
            window!!.pollLast()
        }
        // Insert at the beginning of the window.
        window!!.addFirst(classificationResult)

        val allClasses = HashSet<String>()
        for (result in window!!) {
            allClasses.addAll(result.getAllClasses() as Set<String>)
        }
        val smoothedResult = ClassificationResult()
        for (className in allClasses) {
            var factor = 1f
            var topSum = 0f
            var bottomSum = 0f
            for (result in window!!) {
                val value: Float = result.getClassConfidence(className)
                topSum += factor * value
                bottomSum += factor
                factor = (factor * (1.0 - alpha)).toFloat()
            }
            smoothedResult.putClassConfidence(className, topSum / bottomSum)
        }
        return smoothedResult
    }
}