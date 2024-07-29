package com.z99a.zarifboyevjavohir.vikipediya.di

import com.z99a.zarifboyevjavohir.vikipediya.domain.impl.WikiStatsRepositoryImpl
import com.z99a.zarifboyevjavohir.vikipediya.domain.repository.WikiStatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

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