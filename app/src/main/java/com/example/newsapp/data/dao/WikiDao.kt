package com.example.newsapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.entity.WikiEntity


@Dao
interface WikiDao {
    @Query("SELECT * FROM wiki_entities")
    fun getAllArticles():List<WikiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(entity: WikiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWiki(entity: WikiEntity)
}