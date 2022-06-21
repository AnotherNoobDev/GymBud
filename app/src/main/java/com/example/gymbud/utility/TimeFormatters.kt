package com.example.gymbud.utility


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