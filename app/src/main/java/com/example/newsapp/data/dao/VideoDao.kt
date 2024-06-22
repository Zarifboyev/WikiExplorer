package com.example.newsapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.entity.YouTubeVideo

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<YouTubeVideo>)

    @Query("SELECT * FROM videos")
    suspend fun getAllVideos(): List<YouTubeVideo>
}
