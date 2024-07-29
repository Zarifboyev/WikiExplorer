package com.z99a.zarifboyevjavohir.vikipediya.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object VideoUtils{

}

object DateFormatter {
    fun formatPublishDate(dateStr: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: ParseException) {
            dateStr
        }
    }

    fun formatDuration(duration: String): String {
        val pattern = "PT(\\d+H)?(\\d+M)?(\\d+S)?".toRegex()
        val match = pattern.matchEntire(duration)
        return if (match != null) {
            val hours = match.groupValues[1].removeSuffix("H").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            val minutes = match.groupValues[2].removeSuffix("M").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            val seconds = match.groupValues[3].removeSuffix("S").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            duration
        }
    }
}


object NumberFormatter {
    fun formatViewCount(viewCount: Int): String {
        return when {
            viewCount < 1000 -> viewCount.toString()
            viewCount < 1000000 -> "${(viewCount / 1000)}k views"
            else -> "${(viewCount / 1000000)}M views"
        }
    }
}
