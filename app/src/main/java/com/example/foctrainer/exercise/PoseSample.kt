package com.example.foctrainer.exercise

import com.google.mlkit.vision.common.PointF3D

class PoseSample (name: String?,className: String?,landmarks: MutableList<PointF3D>) {

    private val TAG = "PoseSample"

    private val className: String? = className
    private var embedding: MutableList<PointF3D> = getPoseEmbedding(landmarks) as MutableList<PointF3D>


    fun getClassName(): String? {
        return className
    }

    fun getEmbedding(): MutableList<PointF3D> {
        return embedding
    }

}