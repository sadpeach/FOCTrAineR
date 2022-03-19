package com.example.foctrainer.exercise

import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.foctrainer.R
import android.os.Bundle
import java.lang.Exception
import java.lang.RuntimeException
import androidx.appcompat.app.ActionBar;


class SettingsActivity : AppCompatActivity() {

    val EXTRA_LAUNCH_SOURCE = "extra_launch_source"

    /** Specifies where this activity is launched from.  */
    // CameraX is only available on API 21+
    enum class LaunchSource{

        CAMERAX_LIVE_PREVIEW(
            R.string.pref_screen_title_camerax_live_preview,
            CameraXLivePreviewPreferenceFragment::class.java
        );

        var titleResId = 0
        var prefFragmentClass: Class<CameraXLivePreviewPreferenceFragment>? = null

        constructor(titleResId: Int, prefFragmentClass: Class<CameraXLivePreviewPreferenceFragment>) {
            this.titleResId = titleResId
            this.prefFragmentClass = prefFragmentClass
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val launchSource = intent.getSerializableExtra(EXTRA_LAUNCH_SOURCE) as LaunchSource?
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(launchSource!!.titleResId)
        }
        try {
            if (launchSource != null) {
                fragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.settings_container,
                        launchSource.prefFragmentClass?.getDeclaredConstructor()?.newInstance() as Fragment
                    )
                    .commit()
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}