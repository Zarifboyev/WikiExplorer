package com.example.newsapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.entity.WikiModel


@Dao
interface WikiDao {

    @Query("SELECT * FROM wiki_articles")
    fun getAllArticles():List<WikiModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWikiData(wikiList: List<WikiModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWiki(entity: WikiModel)
}