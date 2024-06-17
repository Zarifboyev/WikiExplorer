package com.example.newsapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.database.WikiDatabase
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.domain.impl.WikiRepositoryImpl
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.domain.service.WikipediaApiService
import com.google.gson.JsonObject
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideWikiDao(database: WikiDatabase): WikiDao {
        return database.getWikiDao()
    }


    @Provides
    fun provideWikipediaApiService(): WikipediaApiService {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/w/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikipediaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJsonObject(): JsonObject {
        return JsonObject()
    }

    @Provides
    @Singleton
    fun provideWikiModels(): List<WikiModel> {
        // Return an empty list or some default data
        return emptyList()
    }


    @[Singleton Provides]
    fun getArticleDao(database: WikiDatabase): ArticleDao = database.getArticleDao()





    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindWikiRepository(
            wikiRepositoryImpl: WikiRepositoryImpl
        ): WikiRepository
    }

}