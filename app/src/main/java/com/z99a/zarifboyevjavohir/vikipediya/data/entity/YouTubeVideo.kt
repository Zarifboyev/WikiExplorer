package com.z99a.zarifboyevjavohir.vikipediya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class YouTubeVideo(
    @PrimaryKey val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val duration: String,
    val viewCount: Long
)
