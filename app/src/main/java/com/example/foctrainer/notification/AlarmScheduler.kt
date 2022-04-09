//package com.example.foctrainer.notification
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import com.example.foctrainer.R
//import com.example.foctrainer.entity.ScheduleModel
//import java.util.*
//import java.util.Calendar.*
//
//object AlarmScheduler {
//
//  fun scheduleAlarmsForReminder(context: Context, reminderData: ReminderData) {
//
//    // get the AlarmManager reference
//    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//    // Schedule the alarms based on the days to administer the medicine
//    val days = context.resources.getStringArray(R.array.days)
//    if (reminderData.days != null) {
//      for (index in reminderData.days!!.indices) {
//
//        val day = reminderData.days!![index]
//        if (day != null) {
//
//          // get the PendingIntent for the alarm
//          val alarmIntent = createPendingIntent(context, reminderData, day)
//
//          // schedule the alarm
//          val dayOfWeek = getDayOfWeek(days, day)
//          scheduleAlarm(reminderData, dayOfWeek, alarmIntent, alarmMgr)
//        }
//      }
//    }
//  }
//
//  /**
//   * Schedules a single alarm
//   */
//  private fun scheduleAlarm(scheduleData: ScheduleModel, dayOfWeek: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {
//
//    // Set up the time to schedule the alarm
//    val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
//    datetimeToAlarm.timeInMillis = System.currentTimeMillis()
//    datetimeToAlarm.set(HOUR_OF_DAY, scheduleData.hour)
//    datetimeToAlarm.set(MINUTE, scheduleData.minute)
//    datetimeToAlarm.set(SECOND, 0)
//    datetimeToAlarm.set(MILLISECOND, 0)
//    datetimeToAlarm.set(DAY_OF_WEEK, dayOfWeek)
//
//    // Compare the datetimeToAlarm to today
//    val today = Calendar.getInstance(Locale.getDefault())
//    if (shouldNotifyToday(dayOfWeek, today, datetimeToAlarm)) {
//
//      // schedule for today
//      alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
//          datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
//      return
//    }
//
//  }
//
//
//  private fun createPendingIntent(context: Context, scheduleData: ScheduleModel, day: String?): PendingIntent? {
//
//    // create the intent using a unique type
//    val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
//      action = context.getString(R.string.action_notify_administer_medication)
//      type = "$day-${scheduleData.title}-${scheduleData.notes}"
//      putExtra(ReminderDialog.KEY_ID, scheduleData.id)
//    }
//
//    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//  }
//
//  /**
//   * Determines if the Alarm should be scheduled for today.
//   *
//   * @param dayOfWeek day of the week as an Int
//   * @param today today's datetime
//   * @param datetimeToAlarm Alarm's datetime
//   */
//  private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
//    return dayOfWeek == today.get(DAY_OF_WEEK) &&
//        today.get(HOUR_OF_DAY) <= datetimeToAlarm.get(HOUR_OF_DAY) &&
//        today.get(MINUTE) <= datetimeToAlarm.get(MINUTE)
//  }
//
//  /**
//   * Updates a notification.
//   * Note: this just calls [AlarmScheduler.scheduleAlarmsForReminder] since
//   * alarms with exact matching pending intents will update if they are already set, otherwise
//   * call [AlarmScheduler.removeAlarmsForReminder] if the medicine has been administered.
//   *
//   * @param context      current application context
//   * @param reminderData ReminderData for the notification
//   */
//  fun updateAlarmsForReminder(context: Context, reminderData: ReminderData) {
//    if (!reminderData.administered) {
//      AlarmScheduler.scheduleAlarmsForReminder(context, reminderData)
//    } else {
//      AlarmScheduler.removeAlarmsForReminder(context, reminderData)
//    }
//  }
//
//  /**
//   * Removes the notification if it was previously scheduled.
//   *
//   * @param context      current application context
//   * @param reminderData ReminderData for the notification
//   */
//  fun removeAlarmsForReminder(context: Context, reminderData: ReminderData) {
//    val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
//    intent.action = context.getString(R.string.action_notify_administer_medication)
//    intent.putExtra(ReminderDialog.KEY_ID, reminderData.id)
//
//    // type must be unique so Intent.filterEquals passes the check to make distinct PendingIntents
//    // Schedule the alarms based on the days to administer the medicine
//    if (reminderData.days != null) {
//      for (i in reminderData.days!!.indices) {
//        val day = reminderData.days!![i]
//
//        if (day != null) {
//          val type = String.format(Locale.getDefault(), "%s-%s-%s-%s", day, reminderData.name, reminderData.medicine, reminderData.type.name)
//
//          intent.type = type
//          val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//          val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//          alarmMgr.cancel(alarmIntent)
//        }
//      }
//    }
//  }
//
//  /**
//   * Returns the int representation for the day of the week.
//   *
//   * @param days      array from resources
//   * @param dayOfWeek String representation of the day e.g "Sunday"
//   * @return [Calendar.DAY_OF_WEEK] for given dayOfWeek
//   */
//  private fun getDayOfWeek(days: Array<String>, dayOfWeek: String): Int {
//    return when {
//      dayOfWeek.equals(days[0], ignoreCase = true) -> SUNDAY
//      dayOfWeek.equals(days[1], ignoreCase = true) -> MONDAY
//      dayOfWeek.equals(days[2], ignoreCase = true) -> TUESDAY
//      dayOfWeek.equals(days[3], ignoreCase = true) -> WEDNESDAY
//      dayOfWeek.equals(days[4], ignoreCase = true) -> THURSDAY
//      dayOfWeek.equals(days[5], ignoreCase = true) -> FRIDAY
//      else -> SATURDAY
//    }
//  }
//
//}