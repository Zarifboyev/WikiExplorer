package com.example.newsapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.database.WikiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @[Provides Singleton]
    fun provideDatabase(@ApplicationContext context: Context): WikiDatabase {
        return Room.databaseBuilder(context, WikiDatabase::class.java, "wiki.db")
            .allowMainThreadQueries().build()
    }


    @[Singleton Provides]
    fun getWikiDao(database: WikiDatabase): WikiDao = database.getWikiDao()

    @[Singleton Provides]
    fun getArticleDao(database: WikiDatabase): ArticleDao = database.getArticleDao()
}