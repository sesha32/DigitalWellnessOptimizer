package com.example.digitalwellness.mooddetection

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer {

    private val faceDetector: FaceDetector

    init {
        // Configure face detection options
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    fun analyzeFace(bitmap: Bitmap, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                // Analyze facial expressions
                val mood = analyzeFaces(faces)
                onSuccess(mood)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    private fun analyzeFaces(faces: List<Face>): String {
        if (faces.isEmpty()) return "No face detected"

        val face = faces[0] // Analyze the first face detected
        val smileProbability = face.smilingProbability ?: 0f
        val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
        val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f

        return when {
            smileProbability > 0.7 -> "Happy"
            leftEyeOpenProbability < 0.3 && rightEyeOpenProbability < 0.3 -> "Sleepy"
            else -> "Neutral"
        }
    }

    fun release() {
        faceDetector.close()
    }
}