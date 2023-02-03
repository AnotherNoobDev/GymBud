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
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.datasource.database.GymBudRoomDatabase
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.data.repository.QuotesRepository
import com.gymbud.gymbud.utility.determineActiveProgramDay
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

            var notificationContent: String = quotesRepository.getQuoteOfTheDay()

            val (programId, programDayIdOrPos, programDayTimestamp) = appRepository.activeProgramAndProgramDay.first()

            // if we have an active program, we can additional say what today's activity will be
            if (programId != ItemIdentifierGenerator.NO_ID && programDayIdOrPos != ItemIdentifierGenerator.NO_ID) {
                val db = GymBudRoomDatabase.getDatabase(context.applicationContext)
                val programsDao = db.programTemplateDao()

                val program = programsDao.get(programId).first()
                if (program != null) {
                    val programDayPos = programDayIdOrPos.toInt()

                    val programItemsDao = db.programTemplateWithItemDao()
                    val items = programItemsDao.getAll(programId).sortedBy { it.programItemPosition }

                    assert(items[0].programItemPosition == 0)
                    assert(items.last().programItemPosition == items.size - 1)

                    val upToDatePos = determineActiveProgramDay(
                        items.map { if (it.isWithWorkoutTemplate()) it.workoutTemplateId!! else it.restPeriodId!! },
                        programDayPos, programDayTimestamp
                    )

                    items[upToDatePos].let {
                        val today = if (it.isWithRestPeriod()) {
                            "Rest Day"
                        } else {
                            val workoutsDao = db.workoutTemplateDao()
                            val workout = workoutsDao.get(it.workoutTemplateId!!).first()

                            workout?.name ?: ""
                        }

                        if (today.isNotEmpty()) {
                            notificationContent += "\n\nToday: $today"
                        }
                    }
                }
            }

            // send notification
            notificationManager.sendReminderNotification(
                context,
                context.getString(R.string.notificationsChannelId),
                notificationContent
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