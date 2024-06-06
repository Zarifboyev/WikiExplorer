package com.example.newsapp.data.di

import com.example.newsapp.domain.repository.ArticleRepository
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.domain.impl.ArticleRepositoryImpl
import com.example.newsapp.domain.impl.WikiRepositoryImpl
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
            wikiRepositoryImpl: WikiRepositoryImpl
        ): WikiRepository
    }

}