package com.gymbud.gymbud

import android.content.Context
import android.content.pm.PackageManager

class AppConfig {
    companion object {
        fun getYoutubeApiKey(context: Context): String {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return appInfo.metaData["youtubeApiKey"].toString()
        }

        const val DATABASE_NAME = "gymbud_database"
    }
}