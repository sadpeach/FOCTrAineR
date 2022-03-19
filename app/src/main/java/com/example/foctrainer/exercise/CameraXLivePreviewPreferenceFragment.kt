package com.example.foctrainer.exercise

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable;
import android.util.Size;
import androidx.annotation.StringRes;
import androidx.preference.PreferenceFragmentCompat
import com.example.foctrainer.R
import com.example.foctrainer.exercise.PreferenceUtils.saveString
import android.hardware.Camera.CameraInfo;
import androidx.preference.ListPreference
import androidx.preference.Preference

class CameraXLivePreviewPreferenceFragment: PreferenceFragmentCompat() {
     private val TAG = "CameraXPreference"

     private fun setUpCameraPreferences() {
        Log.d(TAG,"setting up camera preference..")
        val cameraPreference = findPreference<androidx.preference.PreferenceCategory>(getString(R.string.pref_category_key_camera))
         cameraPreference?.removePreference(
             findPreference(getString(R.string.pref_key_rear_camera_preview_size))
         )
         cameraPreference?.removePreference(
             findPreference(getString(R.string.pref_key_front_camera_preview_size))
         )
        setUpCameraXTargetAnalysisSizePreference(
            R.string.pref_key_camerax_rear_camera_target_resolution, CameraInfo.CAMERA_FACING_BACK
        )
        setUpCameraXTargetAnalysisSizePreference(
            R.string.pref_key_camerax_front_camera_target_resolution,
            CameraInfo.CAMERA_FACING_FRONT
        )
    }

    private fun setUpCameraXTargetAnalysisSizePreference(
        @StringRes previewSizePrefKeyId: Int, lensFacing: Int
    ) {

        Log.d(TAG, "setUpCameraXTargetAnalysisSizePreference ${getString(previewSizePrefKeyId)}, $lensFacing")

        var pref = findPreference<ListPreference>(getString(previewSizePrefKeyId))
        Log.d(TAG, "setUpCameraXTargetAnalysisSizePreference $pref")
        val cameraCharacteristics = activity?.let { getCameraCharacteristics(it, lensFacing) }
        val entries: Array<String?>
        if (cameraCharacteristics != null) {
            val map =
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes: Array<Size> = map!!.getOutputSizes(SurfaceTexture::class.java)
            entries = arrayOfNulls(outputSizes.size)
            for (i in outputSizes.indices) {
                entries[i] = outputSizes[i].toString()
            }
        } else {
            entries = arrayOf(
                "2000x2000",
                "1600x1600",
                "1200x1200",
                "1000x1000",
                "800x800",
                "600x600",
                "400x400",
                "200x200",
                "100x100"
            )
        }
        pref?.entries = entries
        pref?.entryValues = entries
        pref?.summary = if (pref?.entry == null) "Default" else pref?.entry
        Log.d(TAG,"starting sharedpreference listener")
        pref?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d(TAG,"setting sharedpreference using listener")
            val newStringValue = newValue as String
            pref.summary = newStringValue
            activity?.let {
                saveString(
                    it,
                    previewSizePrefKeyId,
                    newStringValue
                )
            }
            true
        }
    }

    @Nullable
    fun getCameraCharacteristics(
        context: Context, lensFacing: Int
    ): CameraCharacteristics? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraList = cameraManager.cameraIdList
            for (availableCameraId in cameraList) {
                Log.d(TAG, "checking camera ID $availableCameraId")
                val availableCameraCharacteristics = cameraManager.getCameraCharacteristics(
                    availableCameraId.toString()
                )
                val availableLensFacing =
                    availableCameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                        ?: continue
                if (availableLensFacing == lensFacing) {
                    return availableCameraCharacteristics
                }
            }
        } catch (e: CameraAccessException) {
        }
        return null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_live_preview_quickstart)
        setUpCameraPreferences()
    }
}