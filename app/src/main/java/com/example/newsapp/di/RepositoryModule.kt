package com.example.newsapp.di

import com.example.newsapp.domain.repository.ArticleRepository
import com.example.newsapp.domain.repository.WikiStatsRepository
import com.example.newsapp.domain.impl.ArticleRepositoryImpl
import com.example.newsapp.domain.impl.WikiStatsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @[Binds Singleton]
    fun provideArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository

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