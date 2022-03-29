//
//package com.example.foctrainer.notification
//
//import android.app.Application
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.lifecycle.ViewModelProvider
//import com.example.foctrainer.MainActivity
//import com.example.foctrainer.R
//import com.example.foctrainer.databaseConfig.FocTrainerApplication
//import com.example.foctrainer.utils.NotifUtils
//import com.example.foctrainer.utils.NotifUtils.application
//import com.example.foctrainer.viewModel.UserViewModel
//import com.example.foctrainer.viewModel.UserViewModelFactory
//
//class AlarmReceiver : BroadcastReceiver() {
//    private lateinit var userViewModel:UserViewModel
//
//
//    override fun onReceive(context: Context, intent: Intent) {
//
//        //Things to do when message is received
//        if (context != null && intent != null && intent.action != null) {
//
//            if (intent.action!!.equals(context.getString(R.string.action_notify_administer_medication), ignoreCase = true)) {
//                if (intent.extras != null) {
//                    NotifUtils.application = context.applicationContext as Application
//                    val reminderData = NotifUtils.getReminderById(intent.extras!!.getInt(ReminderDialog.KEY_ID))
//                    if (reminderData != null) {
//                        NotificationHelper.createNotificationForEvent(context, reminderData)
//                    }
//                }
//            }
//        }
//    }
//}