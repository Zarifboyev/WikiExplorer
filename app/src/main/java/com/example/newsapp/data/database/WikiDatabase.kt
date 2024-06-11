package com.example.newsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.entity.ArticleTestEntity
import com.example.newsapp.data.entity.WikiModel

@Database(entities = [ArticleTestEntity::class, WikiModel::class], version = 1, exportSchema = false)
abstract class WikiDatabase:RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
    abstract fun getWikiDao(): WikiDao
    companion object {
        private const val DATABASE_NAME = "wikipedia_db"

        @Volatile
        private var instance: WikiDatabase? = null

        fun getInstance(context: Context): WikiDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WikiDatabase {
            return databaseBuilder(context, WikiDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }

}