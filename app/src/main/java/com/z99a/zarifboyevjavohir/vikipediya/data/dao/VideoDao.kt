package com.z99a.zarifboyevjavohir.vikipediya.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.z99a.zarifboyevjavohir.vikipediya.data.entity.YouTubeVideo

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<YouTubeVideo>)

    @Query("SELECT * FROM videos")
    suspend fun getAllVideos(): List<YouTubeVideo>
}
