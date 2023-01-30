package com.gymbud.gymbud.features.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.gymbud.gymbud.MainActivity
import com.gymbud.gymbud.R
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.data.repository.QuotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private const val NOTIFICATION_ID = 1


class AlarmReceiver : BroadcastReceiver() {

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            val appRepository = AppRepository(context.applicationContext)
            val quotesRepository = QuotesRepository(context.applicationContext)

            // send notification
            notificationManager.sendReminderNotification(
                context,
                context.getString(R.string.notificationsChannelId),
                quotesRepository.getQuoteOfTheDay()
            )

            // reschedule
            RemindersManager.startReminder(
                context.applicationContext,
                DAILY_WORKOUT_REMINDER_ID,
                appRepository.dailyWorkoutReminderTime.first()
            )
        }
    }
}


fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
    notificationContent: String
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(applicationContext.getString(R.string.dailyWorkoutReminderNotificationTile))
        .setContentText(notificationContent)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
        .setSmallIcon(R.drawable.ic_equipment_24)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}