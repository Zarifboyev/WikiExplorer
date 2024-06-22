package com.example.newsapp.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.newsapp.data.dao.*
import com.example.newsapp.data.entity.*
import java.io.IOException

@Database(entities = [ArticleTestEntity::class, WikiModel::class, TasksEntity::class, YouTubeVideo::class], version = 1, exportSchema = false)
abstract class WikiDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
    abstract fun getWikiDao(): WikiDao
    abstract fun videoDao(): VideoDao
    abstract fun wikiTaskDao(): WikiTaskDao

    companion object {
        private const val DATABASE_NAME = "wikipedia_db"
        private const val TAG = "WikiDatabase"

        @Volatile
        private var instance: WikiDatabase? = null

        fun getInstance(context: Context): WikiDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WikiDatabase {
            return Room.databaseBuilder(context, WikiDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
