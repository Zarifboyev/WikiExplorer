package com.example.newsapp.utils

import android.graphics.Color

class ColorPicker {
    private val colors = listOf(
        "#FF5733", "#33FF57", "#5733FF", "#F0E68C", "#8A2BE2", "#5F9EA0", "#D2691E", "#FF7F50", "#6495ED", "#DC143C"
    ).map { Color.parseColor(it) }

    fun getColorAt(index: Int): Int {
        return colors[index % colors.size]
    }
}
