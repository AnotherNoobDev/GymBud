package com.gymbud.gymbud.features.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*


const val DAILY_WORKOUT_REMINDER_ID = 100


object RemindersManager {

    // if reminder already exists, new reminderTimeInMinutes will be used
    fun startReminder(context: Context, reminderId: Int, reminderTimeInMinutes: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val hours = (reminderTimeInMinutes / 60).toInt()
        val minutes = (reminderTimeInMinutes % 60).toInt()

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                )
            }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
        }

        // If the trigger time you specify is in the past, the alarm triggers immediately.
        // if soo just add one day to required calendar
        // Note: i'm also adding one min cuz if the user clicked on the notification as soon as
        // he receive it it will reschedule the alarm to fire another notification immediately
        if (Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0) {
            calendar.add(Calendar.DATE, 1)
        }

        //Log.d("RemindersManager", "Reminder scheduled for ${SimpleDateFormat.getDateTimeInstance().format(calendar.time)}")

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }


    fun stopReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE
                } else {
                    0
                }
            )
        }

        //Log.d("RemindersManager", "Cancelling reminder")

        alarmManager.cancel(intent)
    }
}