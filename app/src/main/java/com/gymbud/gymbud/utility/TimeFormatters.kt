package com.gymbud.gymbud.utility

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
object TimeFormatter {
    // won't update if user changes locale while app is running, but that's fine for now :)
    @SuppressLint("ConstantLocale")
    private val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

    private val shortDateFormat: SimpleDateFormat = try {
        val datePattern =(dateFormat as SimpleDateFormat).toPattern().replace("y", "").trim { it < 'A' || it > 'z' }
        SimpleDateFormat(datePattern)
    } catch (_: Exception) {
        SimpleDateFormat("dd.mm")
    }


    fun getFormattedTimeMMSS(elapsedSec: Long): String {
        val minutes = elapsedSec / 60
        val sec = elapsedSec % 60

        return String.format("%02d:%02d", minutes, sec)
    }


    fun getFormattedTimeHHMMSS(elapsedSec: Long): String {
        val hours = elapsedSec / 3600
        val remainingSec = elapsedSec % 3600
        val minutes = remainingSec / 60
        val sec = remainingSec % 60

        return String.format("%02d:%02d:%02d", hours, minutes, sec)
    }


    fun getFormattedWallClockHHMM(timeInMinutes: Long, use24hHourFormat: Boolean): String {
        var h = timeInMinutes / 60
        val m = timeInMinutes % 60

        return if (use24hHourFormat) {
            String.format("%02d:%02d", h, m)
        } else {
            val specifier: String

            when {
                h == 0L -> {
                    h = 12
                    specifier = "AM"
                }
                h <= 11L -> {
                    specifier = "AM"
                }
                h == 12L -> {
                    specifier = "PM"
                }
                else -> {
                    h -= 12
                    specifier = "PM"
                }
            }

            String.format("%02d:%02d %s", h, m, specifier)
        }
    }


    fun getFormattedDateDDMMYYYY(date: Date): String {
        return dateFormat.format(date)
    }


    fun getFormattedDateDDMM(date: Date): String {
        return shortDateFormat.format(date)
    }
}
