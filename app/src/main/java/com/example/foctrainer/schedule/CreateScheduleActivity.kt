package com.example.foctrainer.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import com.example.foctrainer.R
import com.example.foctrainer.databinding.ActivityCreateScheduleBinding
import java.util.*


class CreateScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateScheduleBinding

    companion object {
        const val TAG = "CreateScheduleActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //create channel for notification
//        NotificationHelper.createNotificationChannel(this,
//            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
//            getString(R.string.app_name), "App notification channel.")
//
//        binding.setNotification.setOnCheckedChangeListener { buttonView, isChecked
//            // Responds to switch being checked/unchecked
//
//
//
//        }
//
//
//        binding.saveScheduleButton.setOnClickListener(){
//
//        }


    }

    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}

