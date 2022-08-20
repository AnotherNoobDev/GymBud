package com.example.gymbud.utility


object YoutubeHelper {
    private val videoIdFromUrlRegex = "(?:[?&]v=|\\/embed\\/|\\/1\\/|\\/v\\/|https:\\/\\/(?:www\\.)?youtu\\.be\\/)([^&\\n?#]+)".toRegex()

    fun getVideoIdFromURL(url: String): String? {
        val matchResult = videoIdFromUrlRegex.find(url) ?: return null
        if(matchResult.groupValues.size < 2) {
            return null
        }

        return matchResult.groupValues[1]
    }


    fun videoIdToURL(videoId: String): String {
        if (videoId.isEmpty()) {
            return ""
        }

        return "https://youtu.be/$videoId"
    }
}