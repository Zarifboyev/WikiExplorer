package com.example.newsapp.data.di

import com.example.newsapp.domain.ArticleRepository
import com.example.newsapp.domain.WikiRepository
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


    @[Binds Singleton]
    fun provideGroupRepository(impl: WikiRepositoryImpl): WikiRepository


}