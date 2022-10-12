package com.gymbud.gymbud.utility

import java.util.*


const val ONE_DAY_MS = 24 * 3600 * 1000

/**
 * returns a span of time around a given month (use Calendar.JANUARY.. Calendar..DECEMBER), filling with days from the month before and after
 * return value is (start date timestamp, end date timestamp, list of (month, day in month))
 */
fun getMonthSpan(year: Int, month: Int, daySpan: Int): Triple<Long, Long, List<Pair<Int,Int>>> {
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

    val daysInMonth = mutableListOf<Pair<Int,Int>>()
    for (i in 1..daySpan) {
        daysInMonth.add(Pair(calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]))
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val endDate = calendar.time.time

    return Triple(startDate, endDate, daysInMonth)
}


fun getMonth(dateMs: Long): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = Date(dateMs)

    return calendar[Calendar.MONTH]
}


fun getDayOfMonth(dateMs: Long): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = Date(dateMs)

    return calendar[Calendar.DAY_OF_MONTH]
}


fun addDays(nowMs: Long, nDays: Int): Long {
    val c = Calendar.getInstance()
    c.time = Date(nowMs)
    c.add(Calendar.DATE, nDays)

    return c.time.time
}