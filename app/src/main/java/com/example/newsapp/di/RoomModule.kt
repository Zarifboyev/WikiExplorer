package com.example.newsapp.data.di

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.database.WikiDatabase
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.domain.impl.WikiService
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.presentation.viewModels.impl.ProfileViewModelImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
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
    fun provideWikiDao(database: WikiDatabase): WikiDao {
        return database.getWikiDao()
    }

    @[Singleton Provides]
    fun provideWikiService(): WikiService {
        return WikiService()
    }
    @Provides
    @Singleton
    fun provideWikiModels(): List<WikiModel> {
        // Return an empty list or some default data
        return emptyList()
    }
    @[Singleton Provides]
    fun getArticleDao(database: WikiDatabase): ArticleDao = database.getArticleDao()




}