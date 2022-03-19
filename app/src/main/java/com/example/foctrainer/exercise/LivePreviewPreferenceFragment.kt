//package com.example.foctrainer.exercise
//
//import android.hardware.Camera
//import android.os.Bundle
//import android.preference.ListPreference
//import android.preference.Preference
//import android.preference.PreferenceCategory
//import android.preference.PreferenceFragment
//import androidx.annotation.StringRes
//import androidx.preference.PreferenceFragmentCompat
//import com.example.foctrainer.exercise.PreferenceUtils.saveString
//import com.example.foctrainer.exercise.CameraSource
//import com.example.foctrainer.exercise.CameraSource.SizePair
//import com.example.foctrainer.R
//import java.lang.RuntimeException
//import java.util.HashMap
//import java.util.List;
//
//
//
//
///** Configures live preview demo settings.  */
//open class LivePreviewPreferenceFragment : androidx.preference.PreferenceFragment() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        addPreferencesFromResource(R.xml.preference_live_preview_quickstart)
//        setUpCameraPreferences()
//        FaceDetectionUtils.setUpFaceDetectionPreferences(this,  /* isStreamMode = */true)
//    }
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        TODO("Not yet implemented")
//    }
//
//    open fun setUpCameraPreferences() {
//        val cameraPreference =
//            findPreference(getString(R.string.pref_category_key_camera)) as PreferenceCategory?
//        cameraPreference.removePreference(
//            findPreference(getString(R.string.pref_key_camerax_rear_camera_target_resolution))
//        )
//        cameraPreference.removePreference(
//            findPreference(getString(R.string.pref_key_camerax_front_camera_target_resolution))
//        )
//        setUpCameraPreviewSizePreference(
//            R.string.pref_key_rear_camera_preview_size,
//            R.string.pref_key_rear_camera_picture_size,
//            Camera.CameraInfo.CAMERA_FACING_BACK
//        )
//        setUpCameraPreviewSizePreference(
//            R.string.pref_key_front_camera_preview_size,
//            R.string.pref_key_front_camera_picture_size,
//            Camera.CameraInfo.CAMERA_FACING_FRONT
//        )
//    }
//
//    private fun setUpCameraPreviewSizePreference(
//        @StringRes previewSizePrefKeyId: Int, @StringRes pictureSizePrefKeyId: Int, cameraId: Int
//    ) {
//        val previewSizePreference =
//            findPreference(getString(previewSizePrefKeyId)) as ListPreference
//        var camera: Camera? = null
//        try {
//            camera = Camera.open(cameraId)
//            val previewSizeList: List<SizePair> = CameraSource.generateValidPreviewSizeList(camera)
//            val previewSizeStringValues = arrayOfNulls<String>(previewSizeList.size)
//            val previewToPictureSizeStringMap: MutableMap<String, String> = HashMap()
//            for (i in previewSizeList.indices) {
//                val sizePair: SizePair = previewSizeList[i]
//                previewSizeStringValues[i] = sizePair.preview.toString()
//                if (sizePair.picture != null) {
//                    previewToPictureSizeStringMap[sizePair.preview.toString()] =
//                        sizePair.picture.toString()
//                }
//            }
//            previewSizePreference.entries = previewSizeStringValues
//            previewSizePreference.entryValues = previewSizeStringValues
//            if (previewSizePreference.entry == null) {
//                // First time of opening the Settings page.
//                val sizePair: SizePair = CameraSource.selectSizePair(
//                    camera,
//                    CameraSource.DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH,
//                    CameraSource.DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT
//                )
//                val previewSizeString: String = sizePair.preview.toString()
//                previewSizePreference.value = previewSizeString
//                previewSizePreference.summary = previewSizeString
//                saveString(
//                    activity,
//                    pictureSizePrefKeyId,
//                    if (sizePair.picture != null) sizePair.picture.toString() else null
//                )
//            } else {
//                previewSizePreference.summary = previewSizePreference.entry
//            }
//            previewSizePreference.onPreferenceChangeListener =
//                Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any ->
//                    val newPreviewSizeStringValue = newValue as String
//                    previewSizePreference.summary = newPreviewSizeStringValue
//                    saveString(
//                        activity,
//                        pictureSizePrefKeyId,
//                        previewToPictureSizeStringMap[newPreviewSizeStringValue]
//                    )
//                    true
//                }
//        } catch (e: RuntimeException) {
//            // If there's no camera for the given camera id, hide the corresponding preference.
//            (findPreference(getString(R.string.pref_category_key_camera)) as PreferenceCategory)
//                .removePreference(previewSizePreference)
//        } finally {
//            camera?.release()
//        }
//    }
//}