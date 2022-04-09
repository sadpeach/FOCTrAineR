//package com.example.foctrainer.notification
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.foctrainer.MainActivity
//import com.example.foctrainer.R
//import com.example.foctrainer.entity.ScheduleModel
//import com.example.foctrainer.schedule.ScheduleCalendar
//
//object NotificationHelper {
//
//    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            val channelId = "${context.packageName}-$name"
//            val channel = NotificationChannel(channelId, name, importance)
//            channel.description = description
//            channel.setShowBadge(showBadge)
//
//            val notificationManager = context.getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    fun createNotificationForEvent(context: Context, scheduleData: ScheduleModel) {
//
//        val notificationBuilder = buildNotificationForEvent(context, scheduleData)
//        val notificationManager = NotificationManagerCompat.from(context)
//        val administerPendingIntent = createPendingIntentForAction(context, scheduleData)
//        notificationBuilder.addAction(
//            R.drawable.baseline_done_black_24,
//            context.getString(R.string.administer),
//            administerPendingIntent)
//// 4
//        notificationManager.notify(scheduleData.id, notificationBuilder.build())
//    }
//
////    private fun buildGroupNotification(context: Context, reminderData: ReminderData): NotificationCompat.Builder {
////        val channelId = "${context.packageName}-${reminderData.type.name}"
////        return NotificationCompat.Builder(context, channelId).apply {
////            setSmallIcon(R.drawable.ic_stat_medicine)
////            setContentTitle(reminderData.type.name)
////            setContentText(context.getString(R.string.group_notification_for, reminderData.type.name))
////            setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.group_notification_for, reminderData.type.name)))
////            setAutoCancel(true)
////            setGroupSummary(true)
////            setGroup(reminderData.type.name)
////        }
////    }
//
//    private fun buildNotificationForEvent(context: Context, reminderData: ReminderData): NotificationCompat.Builder {
//
//        val channelId = "${context.packageName}-${reminderData.type.name}"
//
//        return NotificationCompat.Builder(context, channelId).apply {
//            setSmallIcon(R.drawable.ic_stat_medicine)
//            setContentTitle(reminderData.name)
//            setAutoCancel(true)
//
//            // get a drawable reference for the LargeIcon
//            val drawable = when (reminderData.type) {
//                ReminderData.PetType.Dog -> R.drawable.dog
//                ReminderData.PetType.Cat -> R.drawable.cat
//                else -> R.drawable.other
//            }
//            setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable))
//            setContentText("${reminderData.medicine}, ${reminderData.desc}")
////            setGroup(reminderData.type.name)
//
//            // note is not important so if it doesn't exist no big deal
//            if (reminderData.note != null) {
//                setStyle(NotificationCompat.BigTextStyle().bigText(reminderData.note))
//            }
//
//            // Launches the app to open the reminder edit screen when tapping the whole notification
//            val intent = Intent(context, MainActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                putExtra(ReminderDialog.KEY_ID, reminderData.id)
//            }
//
//            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//            setContentIntent(pendingIntent)
//        }
//    }
//
//    private fun createPendingIntentForAction(context: Context,scheduleData: ScheduleModel): PendingIntent? {
//        /*
//            Create an Intent to update the ReminderData if Administer action is clicked
//         */
//        val administerIntent = Intent(context, ScheduleCalendar::class.java).apply {
//            action = context.getString(R.string.action_medicine_administered)
//            putExtra(AppGlobalReceiver.NOTIFICATION_ID, scheduleData.id)
//            putExtra(ReminderDialog.KEY_ID, scheduleData.id)
//            putExtra(ReminderDialog.KEY_ADMINISTERED, true)
//        }
//
//        return PendingIntent.getBroadcast(context, ADMINISTER_REQUEST_CODE, administerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//}