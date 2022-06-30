package com.example.gymbud.utility

import java.util.*


private const val ONE_DAY_MS = 24 * 60 * 60 * 1000

/**
 * returns a span of time around a given month (use Calendar.JANUARY.. Calendar..DECEMBER), filling with days from the month before and after
 * return value is [start date timestamp, end date timestamp, list of days in month]
 */
fun getMonthSpan(year: Int, month: Int, daySpan: Int): Triple<Long, Long, List<Int>> {
    val calendar: Calendar = Calendar.getInstance()

    // set month and year
    calendar[Calendar.YEAR] = year
    calendar[Calendar.MONTH] = month

    // determine the cell for current month's beginning
    calendar[Calendar.DAY_OF_MONTH] = 1
    var monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 2
    if (monthBeginningCell < 0) {
        monthBeginningCell += 7
    }

    // move calendar backwards to the beginning of the week
    calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)
    val startDate = calendar.time.time

    val daysInMonth = mutableListOf<Int>()
    for (i in 1..daySpan) {
        daysInMonth.add(calendar[Calendar.DAY_OF_MONTH])
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val endDate = calendar.time.time

    return Triple(startDate, endDate, daysInMonth)
}


fun getDayOfMonth(dateMs: Long): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = Date(dateMs)

    return calendar[Calendar.DAY_OF_MONTH]
}


fun addDays(nowMs: Long, nDays: Int): Long {
    return nowMs + nDays * ONE_DAY_MS
}