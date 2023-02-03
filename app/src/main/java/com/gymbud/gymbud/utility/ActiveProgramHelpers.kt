package com.gymbud.gymbud.utility

import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.model.ItemIdentifier


fun determineActiveProgramDay(programItems: List<ItemIdentifier>, lastProgramDayPos: Int, lastProgramDayTimestamp: Long): Int {
    var daysPast = getDaysPast(System.currentTimeMillis(), lastProgramDayTimestamp)
    var upToDatePos = lastProgramDayPos

    if (daysPast > 0) {
        upToDatePos++
        daysPast--
    }

    while (daysPast > 0) {
        if (upToDatePos >= programItems.size) {
            upToDatePos = 0
        }

        // we can move past Rest Days without user opening the app, but don't auto skip workouts
        if (programItems[upToDatePos] != ItemIdentifierGenerator.REST_DAY_ID) {
            break
        }

        upToDatePos++
        daysPast--
    }

    if (upToDatePos >= programItems.size) {
        upToDatePos = 0
    }

    return upToDatePos
}