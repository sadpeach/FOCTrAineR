package com.example.foctrainer.exercise

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import java.lang.Math.min
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.lang.Math.max


//classifer based on the pose samples
class PoseClassifier {

    private val TAG = "PoseClassifier"

    private var poseSamples: List<PoseSample>? = null
    private var maxDistanceTopK = 0
    private var meanDistanceTopK = 0
    private var axesWeights: PointF3D? = null

    constructor(poseSamples: MutableList<PoseSample?>): this(poseSamples as List<PoseSample>?, 30, 10,  PointF3D.from(1f, 1f, 0.2f))

    constructor(
        poseSamples: List<PoseSample>?, maxDistanceTopK: Int,
        meanDistanceTopK: Int, axesWeights: PointF3D?
    ) {
        this.poseSamples = poseSamples
        this.maxDistanceTopK = maxDistanceTopK
        this.meanDistanceTopK = meanDistanceTopK
        this.axesWeights = axesWeights
    }

    private fun extractPoseLandmarks(pose: Pose): MutableList<PointF3D> {
        val landmarks: MutableList<PointF3D> = ArrayList()
        for (poseLandmark in pose.allPoseLandmarks) {
            landmarks.add(poseLandmark.position3D)
        }
        return landmarks
    }

    /**
     * Returns the max range of confidence values.
     *
     *
     * <Since we calculate confidence by counting></Since>[PoseSample]s that survived
     * outlier-filtering by maxDistanceTopK and meanDistanceTopK, this range is the minimum of two.
     */
    fun confidenceRange(): Int {
        return min(maxDistanceTopK, meanDistanceTopK)
    }

    fun classify(pose: Pose): ClassificationResult? {
        return classify(extractPoseLandmarks(pose))
    }

    fun classify(landmarks: MutableList<PointF3D>): ClassificationResult? {
        val result = ClassificationResult()
        // Return early if no landmarks detected.
        if (landmarks.isEmpty()) {
            return result
        }

        // We do flipping on X-axis so we are horizontal (mirror) invariant.
        val flippedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
        multiplyAll(flippedLandmarks, PointF3D.from(-1f, 1f, 1f))
        val embedding = getPoseEmbedding(landmarks)
        val flippedEmbedding = getPoseEmbedding(flippedLandmarks)


        // Classification is done in two stages:
        //  * First we pick top-K samples by MAX distance. It allows to remove samples that are almost
        //    the same as given pose, but maybe has few joints bent in the other direction.
        //  * Then we pick top-K samples by MEAN distance. After outliers are removed, we pick samples
        //    that are closest by average.

        // Keeps max distance on top so we can pop it when top_k size is reached.
        val maxDistances: PriorityQueue<Pair<PoseSample, Float>> = PriorityQueue(
            maxDistanceTopK
        ) { o1, o2 -> -java.lang.Float.compare(o1.second, o2.second) }
        // Retrieve top K poseSamples by least distance to remove outliers.
        for (poseSample in poseSamples!!) {
            val sampleEmbedding: MutableList<PointF3D> = poseSample.getEmbedding()
            var originalMax = 0f
            var flippedMax = 0f
            if (embedding != null) {
                for (i in embedding.indices) {
                    originalMax = max(
                        originalMax,
                        maxAbs(multiply(subtract(embedding?.get(i)!!, sampleEmbedding[i]), axesWeights!!))
                    )
                    flippedMax = max(
                        flippedMax,
                        maxAbs(
                            multiply(
                                subtract(flippedEmbedding?.get(i)!!, sampleEmbedding[i]), axesWeights!!
                            )
                        )
                    )
                }
            }
            // Set the max distance as min of original and flipped max distance.
            maxDistances.add(Pair(poseSample, min(originalMax, flippedMax)))
            // We only want to retain top n so pop the highest distance.
            if (maxDistances.size > maxDistanceTopK) {
                maxDistances.poll()
            }
        }

        // Keeps higher mean distances on top so we can pop it when top_k size is reached.
        val meanDistances: PriorityQueue<Pair<PoseSample, Float>> = PriorityQueue(
            meanDistanceTopK
        ) { o1, o2 -> -java.lang.Float.compare(o1.second, o2.second) }
        // Retrive top K poseSamples by least mean distance to remove outliers.
        for ((poseSample) in maxDistances) {
            val sampleEmbedding: MutableList<PointF3D> = poseSample.getEmbedding()
            var originalSum = 0f
            var flippedSum = 0f
            if (embedding != null) {
                for (i in embedding.indices) {
                    originalSum += sumAbs(
                        multiply(
                            sampleEmbedding?.get(i)?.let { subtract(embedding?.get(i)!!, it) }, axesWeights!!
                        )
                    )
                    flippedSum += sumAbs(
                        multiply(sampleEmbedding?.get(i)?.let { subtract(flippedEmbedding?.get(i)!!, it) }, axesWeights!!)
                    )
                }
            }
            // Set the mean distance as min of original and flipped mean distances.
            val meanDistance: Float = min(originalSum, flippedSum) / (embedding?.size!! * 2)
            meanDistances.add(Pair(poseSample, meanDistance))
            // We only want to retain top k so pop the highest mean distance.
            if (meanDistances.size > meanDistanceTopK) {
                meanDistances.poll()
            }
        }
        for ((first) in meanDistances) {
            val className: String? = first.getClassName()
            if (className != null) {
                result.incrementClassConfidence(className)
            }
        }
        return result
    }
}