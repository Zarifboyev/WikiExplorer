package com.example.newsapp.data.di

import com.example.newsapp.domain.CategoryRepository
import com.example.newsapp.domain.NoteRepository
import com.example.newsapp.domain.impl.CategoryRepositoryImpl
import com.example.newsapp.domain.impl.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @[Binds Singleton]
    fun provideCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository


    @[Binds Singleton]
    fun provideGroupRepository(impl: NoteRepositoryImpl): NoteRepository


}