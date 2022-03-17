package com.example.foctrainer.exercise


class ClassificationResult {
    private var classConfidences: HashMap <String, Float>;

   init{
        classConfidences = HashMap <String, Float>()
    }

    fun getAllClasses(): Set<String?>? {
        return classConfidences.keys
    }

    fun getClassConfidence(className: String?): Float {
        return if (classConfidences.containsKey(className)) classConfidences[className]!! else 0f
    }

    fun getMaxConfidenceClass(): String {
        return classConfidences.maxByOrNull { it.value }!!.key
    }

    fun incrementClassConfidence(className: String) {
        classConfidences[className] =
            (if (classConfidences.containsKey(className)) classConfidences[className]!! + 1 else 1) as Float
    }

    fun putClassConfidence(className: String, confidence: Float) {
        classConfidences[className] = confidence
    }
}