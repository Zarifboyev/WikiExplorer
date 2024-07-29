package com.z99a.zarifboyevjavohir.vikipediya.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.z99a.zarifboyevjavohir.vikipediya.data.SharedPreferencesManager
import com.z99a.zarifboyevjavohir.vikipediya.data.dao.VideoDao
import com.z99a.zarifboyevjavohir.vikipediya.data.dao.WikiDao
import com.z99a.zarifboyevjavohir.vikipediya.data.database.WikiDatabase
import com.z99a.zarifboyevjavohir.vikipediya.domain.impl.WikiStatsRepositoryImpl
import com.z99a.zarifboyevjavohir.vikipediya.domain.repository.WikiStatsRepository
import com.z99a.zarifboyevjavohir.vikipediya.domain.repository.YouTubeRepository
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.WikipediaApiService
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.YouTubeServiceReady
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/"
    private const val WIKIPEDIA_BASE_URL = "https://uz.wikipedia.org/w/"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class YouTubeRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WikipediaRetrofit

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WikiDatabase {
        return Room.databaseBuilder(context, WikiDatabase::class.java, "wiki.db")
            .build()
    }

    @Provides
    fun provideVideoDao(database: WikiDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    @YouTubeRetrofit
    fun provideYouTubeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @WikipediaRetrofit
    fun provideWikipediaRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WIKIPEDIA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeApiService(@YouTubeRetrofit retrofit: Retrofit): YouTubeServiceReady {
        return retrofit.create(YouTubeServiceReady::class.java)
    }

    @Provides
    @Singleton
    fun provideYouTubeRepository(apiService: YouTubeServiceReady): YouTubeRepository {
        return YouTubeRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideWikiStatsRepository(@WikipediaRetrofit retrofit: Retrofit): WikiStatsRepositoryImpl {
        return WikiStatsRepositoryImpl(retrofit.create(WikipediaApiService::class.java))
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context, gson: Gson): SharedPreferencesManager {
        return SharedPreferencesManager(context, gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideWikiDao(database: WikiDatabase): WikiDao {
        return database.getWikiDao()
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
}
