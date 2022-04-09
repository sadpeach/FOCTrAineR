package com.example.foctrainer.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import androidx.camera.core.CameraSelector
import android.os.Build.VERSION_CODES
import android.util.Log
import android.util.Size
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.util.Preconditions
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.example.foctrainer.R
import java.lang.Exception

object PreferenceUtils {
    private val TAG = "PreferenceUtils"

    fun saveString(context: Context, @StringRes prefKeyId: Int, @Nullable value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(context.getString(prefKeyId), value)
            .apply()
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(VERSION_CODES.LOLLIPOP)
    @Nullable
    fun getCameraXTargetResolution(context: Context, lensfacing: Int): Size? {
        Preconditions.checkArgument(
            (
                    lensfacing == CameraSelector.LENS_FACING_BACK
                            || lensfacing == CameraSelector.LENS_FACING_FRONT)
        )
        val prefKey: String =
            if (lensfacing == CameraSelector.LENS_FACING_BACK) context.getString(R.string.pref_key_camerax_rear_camera_target_resolution) else context.getString(
                R.string.pref_key_camerax_front_camera_target_resolution
            )
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        try {
            return android.util.Size.parseSize(sharedPreferences.getString(prefKey, null));
        } catch (e: Exception) {
            return null
        }
    }

    fun shouldHideDetectionInfo(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey: String = context.getString(R.string.pref_key_info_hide)
        return false
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase? {
        val preferGPU = preferGPUForPoseDetection(context)
        val builder =
            PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        if (preferGPU) {
            builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
        }
        return builder.build()

    }

    fun preferGPUForPoseDetection(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("prefKey", true)
    }


    fun isCameraLiveViewportEnabled(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey: String = context.getString(R.string.pref_key_camera_live_viewport)
        return sharedPreferences.getBoolean(prefKey, false)
    }
}