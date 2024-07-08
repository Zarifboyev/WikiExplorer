package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.dao.VideoDao
import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.database.WikiDatabase
import com.example.newsapp.domain.impl.WikiStatsRepositoryImpl
import com.example.newsapp.domain.repository.WikiStatsRepository
import com.example.newsapp.domain.repository.YouTubeRepository
import com.example.newsapp.domain.service.WikipediaApiService
import com.example.newsapp.domain.service.YouTubeServiceReady
import com.example.newsapp.utils.LocationManager
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

object RoomModule {
    private const val BASE_URL = "https://www.googleapis.com/"


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WikiDatabase {
        return Room.databaseBuilder(context, WikiDatabase::class.java, "wiki.db")
            .build()
    }
    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return LocationManager(context)
    }
    @Provides
    fun provideVideoDao(database: WikiDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    fun provideYouTubeApiService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    @Provides
    @Singleton
    fun provideYouTubeRepository(apiService: YouTubeServiceReady): YouTubeRepository {
        return YouTubeRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideWikiStatsRepository(apiService: WikipediaApiService): WikiStatsRepositoryImpl {
        return WikiStatsRepositoryImpl(apiService)
    }



    @Provides
    fun provideWikipediaApiService(): WikipediaApiService {
        return Retrofit.Builder()
            .baseUrl("https://uz.wikipedia.org/w/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikipediaApiService::class.java)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindWikiRepository(
            wikiRepositoryImpl: WikiStatsRepositoryImpl
        ): WikiStatsRepository
    }


    @[Singleton Provides]
    fun provideWikiDao(database: WikiDatabase): WikiDao {
        return database.getWikiDao()
    }


}
