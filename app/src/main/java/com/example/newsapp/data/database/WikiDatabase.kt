package com.example.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.entity.ArticleTestEntity

@Database(entities = [ArticleTestEntity::class, WikiEntity::class], version = 1, exportSchema = false)
abstract class WikiDatabase :RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
    abstract fun getWikiDao(): WikiDao
}